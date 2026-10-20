package com.vacation.tripinmind.repository

import com.vacation.tripinmind.model.Vacation
import kotlinx.coroutines.flow.Flow

interface VacationRepository {
    fun observeVacations(): Flow<List<Vacation>>
    fun getLocalItems(): Flow<List<Vacation>>
    fun getItemById(id: String): Flow<Vacation?>
    suspend fun insertItem(item: Vacation) : Result<Unit>
    suspend fun deleteItem(item: Vacation) : Result<Unit>
    suspend fun updateItem(item: Vacation) : Result<Unit>
    fun getSharedVacationsFlow(): Flow<List<Vacation>>
    fun getVacationById(id: String, sharedVacation: Boolean): Flow<Vacation?>
}
