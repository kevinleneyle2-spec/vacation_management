package com.vacation.tripinmind.data.remote.source

import com.vacation.tripinmind.data.remote.model.VacationDto
import kotlinx.coroutines.flow.Flow

interface VacationRemoteDataSource {
    suspend fun saveVacation(vacation: VacationDto)
    suspend fun deleteVacation(id: String)
    suspend fun getVacationById(id: String): VacationDto?
    suspend fun getVacationsByUserId(userId: String): List<VacationDto>
    fun observeSharedVacations(userId: String): Flow<List<VacationDto>>
}