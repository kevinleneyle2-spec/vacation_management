package com.vacation.tripinmind.repository

import com.vacation.tripinmind.model.UserProfile

interface UserProfileRepository {
    suspend fun getItemById(uuid: String): UserProfile?

    suspend fun getItemByCode(shareCode: String): Result<UserProfile?>

    suspend fun insertItem(): Result<String?>
}