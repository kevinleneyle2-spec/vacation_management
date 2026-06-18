package com.vacation.tripinmind.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.vacation.tripinmind.data.local.interfaces.VacationDao
import com.vacation.tripinmind.data.local.interfaces.VacationInterface
import com.vacation.tripinmind.data.local.model.VacationDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VacationRepository(
    private val vacationDao: VacationDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : VacationInterface {

    private val vacationCollection = firestore.collection("vacation")

    override fun getAllItems(): Flow<List<VacationDto>> = channelFlow {
        launch {
            try {
                val currentUserId = firebaseAuth.uid

                val vacations = vacationCollection.where(
                    Filter.equalTo("createdBy", currentUserId)
                ).get().await()

                val vacationsMap = vacations.toObjects(VacationDto::class.java)
                    .associateBy { it.id }

                for (vacation in vacationsMap) {
                    val result = vacationDao.insertItem(vacation.value)
                    if (result == -1L) {
                        vacationDao.updateItem(vacation.value)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        vacationDao.getAllItems().collect { localItems ->
            send(localItems)
        }
    }

    override fun getItemById(id: String): Flow<VacationDto?> =
        vacationDao.getItemById(id)

    override suspend fun insertItem(item: VacationDto) {
        val finalItem = if (item.id.isEmpty()) {
            val newId = vacationCollection.document().id
            item.copy(id = newId)
        } else {
            item
        }

        vacationDao.insertItem(finalItem)
        try {
            vacationCollection.document(finalItem.id).set(finalItem).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteItem(item: VacationDto) {
        vacationDao.deleteItem(item)
        try {
            vacationCollection.document(item.id).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateItem(item: VacationDto) {
        vacationDao.updateItem(item)
        try {
            vacationCollection.document(item.id).set(item).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
