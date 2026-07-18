package com.vacation.tripinmind.data.repository

import androidx.core.util.remove
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.vacation.tripinmind.data.local.interfaces.VacationDao
import com.vacation.tripinmind.data.local.interfaces.VacationInterface
import com.vacation.tripinmind.data.local.model.VacationDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VacationRepository(
    private val vacationDao: VacationDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : VacationInterface {

    private val vacationCollection = firestore.collection("vacation")

    override fun getAllItems(): Flow<List<VacationDto>> {
        syncRemoteVacations()
        return vacationDao.getAllItems()
    }

    private fun syncRemoteVacations() {
        val currentUserId = firebaseAuth.uid ?: return

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val vacations = vacationCollection.where(
                    Filter.equalTo("createdBy", currentUserId)
                ).get().await()

                val remoteVacations = vacations.toObjects(VacationDto::class.java)

                remoteVacations.forEach { vacation ->
                    val result = vacationDao.insertItem(vacation)
                    if (result == -1L) {
                        vacationDao.updateItem(vacation)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getLocalItems(): Flow<List<VacationDto>> = vacationDao.getAllItems()

    override fun getItemById(id: String): Flow<VacationDto?> {
        syncRemoteVacationById(id)

        return vacationDao.getItemById(id)
    }

    private fun syncRemoteVacationById(id: String) {
        @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val snapshot = vacationCollection.document(id).get().await()
                val remoteVacation = snapshot.toObject(VacationDto::class.java)

                if (remoteVacation != null) {
                    val result = vacationDao.insertItem(remoteVacation)
                    if (result == -1L) {
                        vacationDao.updateItem(remoteVacation)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

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

    fun getVacationById(id: String, sharedVacation: Boolean): Flow<VacationDto?> {
        return if (sharedVacation) {
            getSharedVacationById(id)
        } else {
            getItemById(id)
        }
    }

    private fun getSharedVacationById(id: String): Flow<VacationDto?> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val subscription = vacationCollection.where(
            Filter.arrayContains("shareWithUid", uid)
        ).whereEqualTo("id", id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val vacation = snapshot.toObjects(VacationDto::class.java).firstOrNull()
                    trySend(vacation)
                }
            }

        awaitClose { subscription.remove() }
    }

    fun getSharedVacationsFlow(): Flow<List<VacationDto>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val query = vacationCollection.where(
            Filter.arrayContains("shareWithUid", uid)
        )

        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val vacations = snapshot.toObjects(VacationDto::class.java)
                trySend(vacations)
            }
        }

        awaitClose { subscription.remove() }
    }
}
