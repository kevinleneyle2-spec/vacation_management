package com.vacation.tripinmind.data.local.interfaces

import com.vacation.tripinmind.data.local.model.UserProfileDto
import kotlinx.coroutines.flow.Flow

interface UserProfileInterface {
    suspend fun getItemById(uuid: String): UserProfileDto?

    suspend fun getItemByCode(shareCode: String): UserProfileDto?

    suspend fun insertItem(): String?
}