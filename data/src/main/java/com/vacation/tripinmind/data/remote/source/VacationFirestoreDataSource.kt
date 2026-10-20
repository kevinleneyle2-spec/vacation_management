package com.vacation.tripinmind.data.remote.source

import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.vacation.tripinmind.data.remote.model.VacationDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class VacationFirestoreDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : VacationRemoteDataSource {

    private val vacationCollection = firestore.collection("vacation")

    override suspend fun saveVacation(vacation: VacationDto) {
        vacationCollection.document(vacation.id).set(vacation).await()
    }

    override suspend fun deleteVacation(id: String) {
        vacationCollection.document(id).delete().await()
    }

    override suspend fun getVacationById(id: String): VacationDto? {
        val snapshot = vacationCollection.document(id).get().await()
        return snapshot.toObject(VacationDto::class.java)
    }

    override suspend fun getVacationsByUserId(userId: String): List<VacationDto> {
        val snapshot = vacationCollection
            .where(Filter.equalTo("createdBy", userId))
            .get()
            .await()
        return snapshot.toObjects(VacationDto::class.java)
    }

    override fun observeSharedVacations(userId: String): Flow<List<VacationDto>> = callbackFlow {
        val query = vacationCollection.where(Filter.arrayContains("shareWithUid", userId))
        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val vacations = snapshot?.toObjects(VacationDto::class.java) ?: emptyList()
            trySend(vacations)
        }
        awaitClose { subscription.remove() }
    }
}