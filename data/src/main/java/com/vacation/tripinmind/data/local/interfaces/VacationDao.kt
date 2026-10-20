package com.vacation.tripinmind.data.local.interfaces

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.vacation.tripinmind.data.local.model.VacationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VacationDao {
    @Query("SELECT * from vacation ORDER BY startDate ASC")
    fun getAllItems(): Flow<List<VacationEntity>>

    @Query("SELECT * from vacation WHERE id = :id")
    fun getItemById(id: String): Flow<VacationEntity?>

    @Insert(onConflict = IGNORE)
    suspend fun insertItem(item: VacationEntity): Long

    @Delete
    suspend fun deleteItem(item: VacationEntity)

    @Update
    suspend fun updateItem(item: VacationEntity)

    @Upsert
    suspend fun upsertItem(item: VacationEntity)
}