package com.vacation.tripinmind.data.remote.source

import com.vacation.tripinmind.data.remote.model.UserProfileDto
import com.vacation.tripinmind.data.remote.model.VacationDto
import kotlinx.coroutines.flow.Flow

interface UserProfileRemoteDataSource {
    suspend fun getUserProfileByShareCode(shareCode: String) : UserProfileDto?
    suspend fun getUserProfilesByUid(uid: String): List<UserProfileDto>
    suspend fun addUserProfile(newCode: String, data: Map<String, String>)
}