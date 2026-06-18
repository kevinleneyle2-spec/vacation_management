package com.vacation.tripinmind.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.vacation.tripinmind.data.local.interfaces.UserProfileDao
import com.vacation.tripinmind.data.local.model.UserProfileDto

@Database(entities = [UserProfileDto::class], version = 1, exportSchema = false)
abstract class UserProfileDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var Instance: UserProfileDatabase? = null

        fun getDatabase(context: Context): UserProfileDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    UserProfileDatabase::class.java,
                    "user_profile_database"
                )
                    .build()
                    .also { Instance = it }
            }
        }
    }
}