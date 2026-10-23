package com.vacation.tripinmind.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vacation.tripinmind.data.local.interfaces.UserProfileDao
import com.vacation.tripinmind.data.local.model.UserProfileEntity
import com.vacation.tripinmind.data.mapper.toDomain
import com.vacation.tripinmind.data.remote.source.UserProfileRemoteDataSource
import com.vacation.tripinmind.data.utils.ShareCodeGenerator
import com.vacation.tripinmind.model.UserProfile
import com.vacation.tripinmind.repository.UserProfileRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val remoteDataSource: UserProfileRemoteDataSource,
    private val firebaseAuth: FirebaseAuth
) : UserProfileRepository {

    override suspend fun getItemByCode(shareCode: String): Result<UserProfile?> {
        if (firebaseAuth.currentUser == null) {
            try {
                firebaseAuth.signInAnonymously().await()
            } catch (_: Exception) {
                // ignore
            }
        }
        return try {
            val userProfile = remoteDataSource.getUserProfileByShareCode(shareCode)
            Result.success(userProfile?.toDomain())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getItemById(uuid: String): UserProfile? =
        userProfileDao.getItemById(uuid)?.toDomain()

    override suspend fun insertItem(): Result<String?> {
        val uid = firebaseAuth.currentUser?.uid ?: return Result.success(null)

        val localProfile = userProfileDao.getItemById(uid)
        if (localProfile != null) {
            return Result.success(localProfile.shareCode)
        }

        return try {
            val shareCodeVacation = remoteDataSource.getUserProfilesByUid(uid)

            if (!shareCodeVacation.isEmpty()) {
                val doc = shareCodeVacation.first()
                val code = doc.shareCode

                val profile = UserProfileEntity(uuid = uid, shareCode = code)
                userProfileDao.insertItem(profile)
                Result.success(code)
            } else {
                val newCode = ShareCodeGenerator.generate()

                val data = mapOf(
                    "uuid" to uid,
                    "shareCode" to newCode
                )

                remoteDataSource.addUserProfile(newCode, data)
                userProfileDao.insertItem(UserProfileEntity(uuid = uid, shareCode = newCode))
                Result.success(newCode)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
