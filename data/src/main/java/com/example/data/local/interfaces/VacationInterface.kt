package com.example.data.local.interfaces

import androidx.room.Dao
import com.example.data.local.model.VacationDto
import kotlinx.coroutines.flow.Flow

@Dao
interface VacationInterface {

    fun getAllItems(): Flow<List<VacationDto>>

    fun getItemById(id: Int): Flow<VacationDto>

    suspend fun insertItem(item: VacationDto)

    suspend fun deleteItem(item: VacationDto)

    suspend fun updateItem(item: VacationDto)
}