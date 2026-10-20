package com.vacation.tripinmind.data.remote.source

import com.google.firebase.firestore.FirebaseFirestore
import com.vacation.tripinmind.data.remote.model.UserProfileDto
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserProfileFirestoreDataSource @Inject constructor(
    firestore: FirebaseFirestore
) : UserProfileRemoteDataSource {

    private val userProfileCollection = firestore.collection("userProfile")

    override suspend fun getUserProfileByShareCode(shareCode: String): UserProfileDto? {
        val snapshot = userProfileCollection.document(shareCode).get().await()
        return if (snapshot.exists()) {
            snapshot.toObject(UserProfileDto::class.java)
        } else {
            null
        }
    }

    override suspend fun getUserProfilesByUid(uid: String): List<UserProfileDto> {
        val snapshot = userProfileCollection.whereEqualTo("uuid", uid)
            .get()
            .await()
        return if (!snapshot.isEmpty) {
            snapshot.toObjects(UserProfileDto::class.java)
        } else {
            listOf()
        }
    }

    override suspend fun addUserProfile(newCode: String, data: Map<String, String>) {
        userProfileCollection.document(newCode).set(data).await()

    }
}