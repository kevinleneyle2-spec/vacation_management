package com.vacation.tripinmind.data.local.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vacation.tripinmind.data.local.model.UserProfileDto
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * from user_profile WHERE shareCode = :shareCode")
    suspend fun getItemByCode(shareCode: String): UserProfileDto?

    @Query("SELECT * from user_profile WHERE uuid = :uuid")
    suspend fun getItemById(uuid: String): UserProfileDto?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: UserProfileDto)
}