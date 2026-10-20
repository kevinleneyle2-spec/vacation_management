package com.vacation.tripinmind.data.utils

import androidx.room.TypeConverter
import com.vacation.tripinmind.data.local.model.DayEntity
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @TypeConverter
    fun fromDayList(value: List<DayEntity>?): String {
        if (value == null) return "[]"
        return try {
            json.encodeToString(value)
        } catch (e: Exception) {
            "[]"
        }
    }

    @TypeConverter
    fun toDayList(value: String?): List<DayEntity> {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        if (value == null) return "[]"
        return try {
            json.encodeToString(value)
        } catch (e: Exception) {
            "[]"
        }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}