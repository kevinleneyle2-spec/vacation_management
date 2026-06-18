package com.vacation.tripinmind.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vacation.tripinmind.data.local.Converters
import com.vacation.tripinmind.data.local.interfaces.VacationDao
import com.vacation.tripinmind.data.local.model.VacationDto

@Database(entities = [VacationDto::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class VacationDatabase : RoomDatabase() {

    abstract fun vacationDao(): VacationDao

    companion object {
        @Volatile
        private var Instance: VacationDatabase? = null

        fun getDatabase(context: Context): VacationDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, VacationDatabase::class.java, "vacation_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}