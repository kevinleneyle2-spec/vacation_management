package com.vacation.tripinmind.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vacation.tripinmind.data.local.interfaces.UserProfileDao
import com.vacation.tripinmind.data.local.interfaces.UserProfileInterface
import com.vacation.tripinmind.data.local.model.UserProfileDto
import com.vacation.tripinmind.data.utils.ShareCodeGenerator
import kotlinx.coroutines.tasks.await

class UserProfileRepository(
    private val userProfileDao: UserProfileDao,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : UserProfileInterface {
    private val shareVacationCollection = firestore.collection("userProfile")

    override suspend fun getItemByCode(shareCode: String): UserProfileDto? {
        val shareCodeVacation = shareVacationCollection
            .whereEqualTo("shareCode", shareCode)
            .get()
            .await()

        return if (!shareCodeVacation.isEmpty) {
            val doc = shareCodeVacation.documents.first()
            val code = doc.getString("shareCode") ?: ""
            val uid = doc.getString("uuid") ?: ""

            UserProfileDto(uuid = uid, shareCode = code)
        } else {
            null
        }
    }

    override suspend fun getItemById(uuid: String): UserProfileDto? =
        userProfileDao.getItemById(uuid)

    override suspend fun insertItem(): String? {
        val uid = firebaseAuth.currentUser?.uid ?: return null

        val localProfile = userProfileDao.getItemById(uid)
        if (localProfile != null) {
            return localProfile.shareCode
        }

        return try {
            val shareCodeVacation = shareVacationCollection
                .whereEqualTo("uuid", uid)
                .get()
                .await()

            if (!shareCodeVacation.isEmpty) {
                val doc = shareCodeVacation.documents.first()
                val code = doc.getString("shareCode") ?: ""

                val profile = UserProfileDto(uuid = uid, shareCode = code)
                userProfileDao.insertItem(profile)
                code
            } else {
                val newCode = ShareCodeGenerator.generate()

                val data = mapOf(
                    "uuid" to uid,
                    "shareCode" to newCode
                )

                shareVacationCollection.document(newCode).set(data).await()
                userProfileDao.insertItem(UserProfileDto(uuid = uid, shareCode = newCode))
                newCode
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
