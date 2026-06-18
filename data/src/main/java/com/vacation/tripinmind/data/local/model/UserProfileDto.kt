package com.vacation.tripinmind.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileDto(
    @PrimaryKey val uuid: String = "",
    val shareCode: String = ""
)