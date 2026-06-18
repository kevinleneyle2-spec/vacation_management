package com.vacation.tripinmind.data.local.interfaces

import com.vacation.tripinmind.data.local.model.VacationDto
import kotlinx.coroutines.flow.Flow

interface VacationInterface {

    fun getAllItems(): Flow<List<VacationDto>>

    fun getItemById(id: String): Flow<VacationDto?>

    suspend fun insertItem(item: VacationDto)

    suspend fun deleteItem(item: VacationDto)

    suspend fun updateItem(item: VacationDto)
}