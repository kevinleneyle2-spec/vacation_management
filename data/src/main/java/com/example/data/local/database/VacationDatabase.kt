package com.example.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.Converters
import com.example.data.local.interfaces.VacationDao
import com.example.data.local.model.VacationDto

@Database(entities = [VacationDto::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class VacationDatabase : RoomDatabase() {

    abstract fun vacationDao(): VacationDao

    companion object {
        @Volatile
        private var Instance: VacationDatabase? = null

        fun getDatabase(context: Context): VacationDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, VacationDatabase::class.java, "vacation_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}