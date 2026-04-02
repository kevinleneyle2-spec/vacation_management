package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.local.model.Day
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromDayList(value: List<Day>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toDayList(value: String): List<Day> {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return json.decodeFromString(value)
    }
}