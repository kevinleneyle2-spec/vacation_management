package com.example.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vacation")
data class VacationDto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String,
    @ColumnInfo val startDate: String,
    @ColumnInfo val nbrDay: Int,
    @ColumnInfo val days: List<Day>,
    @ColumnInfo val ideas: List<String>,
    @ColumnInfo val image: String,
    @ColumnInfo val isArchived: Boolean
)