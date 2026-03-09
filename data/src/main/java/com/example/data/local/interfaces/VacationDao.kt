package com.example.data.local.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.model.VacationDto
import kotlinx.coroutines.flow.Flow

@Dao
interface VacationDao {
    @Query("SELECT * from vacation ORDER BY id ASC")
    fun getAllItems(): Flow<List<VacationDto>>

    @Query("SELECT * from vacation WHERE id = :id")
    fun getItemById(id: Int): Flow<VacationDto>

    @Insert(onConflict = IGNORE)
    suspend fun insertItem(item: VacationDto)

    @Delete
    suspend fun deleteItem(item: VacationDto)

    @Update
    suspend fun updateItem(item: VacationDto)
}