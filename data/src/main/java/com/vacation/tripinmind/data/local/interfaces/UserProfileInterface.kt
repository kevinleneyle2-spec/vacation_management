package com.vacation.tripinmind.data.local.interfaces

import com.vacation.tripinmind.data.local.model.UserProfileDto
import kotlinx.coroutines.flow.Flow

interface UserProfileInterface {
    fun getItemById(uuid: String): Flow<UserProfileDto?>

    fun getItemByCode(shareCode: String): Flow<UserProfileDto?>

    suspend fun insertItem(): String?
}