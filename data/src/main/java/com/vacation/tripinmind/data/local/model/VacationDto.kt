package com.vacation.tripinmind.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacation")
data class VacationDto(
    @PrimaryKey val id: String = "",
    @ColumnInfo val name: String = "",
    @ColumnInfo val startDate: Long = 0L,
    @ColumnInfo val endDate: Long = 0L,
    @ColumnInfo val days: List<Day> = listOf(),
    @ColumnInfo val ideas: List<String> = listOf(),
    @ColumnInfo val image: String = "",
    @ColumnInfo val isArchived: Boolean = false,
    @ColumnInfo val createdBy: String = "",
    @ColumnInfo val shareWith: List<String> = listOf(),
    @ColumnInfo val shareWithUid: List<String> = listOf()
) {
    val nbrDay: Int
        get() = if (startDate > 0L && endDate >= startDate) {
            ((endDate - startDate) / (1000 * 60 * 60 * 24)).toInt() + 1
        } else {
            days.size
        }
}