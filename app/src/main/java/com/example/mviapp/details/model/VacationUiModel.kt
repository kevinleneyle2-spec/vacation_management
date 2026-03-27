package com.example.mviapp.details.model

data class VacationUiModel(
    val id: Int,
    val name: String,
    val days: List<DayUiModel>
)

data class DayUiModel(
    val name: String,
    val activities: List<ActivityUiModel>
)

data class ActivityUiModel(
    val name: String,
    val time: String,
    val duration: String
)