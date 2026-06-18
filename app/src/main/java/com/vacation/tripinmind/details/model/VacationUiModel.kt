package com.vacation.tripinmind.details.model

data class VacationUiModel(
    val id: String,
    val name: String,
    val days: List<DayUiModel>,
    val ideas: List<String>
)

data class DayUiModel(
    val name: String,
    val additionalInfo: String,
    val activities: List<ActivityUiModel>
)

data class ActivityUiModel(
    val name: String,
    val location: String,
    val time: String,
    val duration: String
)