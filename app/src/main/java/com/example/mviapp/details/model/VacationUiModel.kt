package com.example.mviapp.details.model

import android.hardware.SensorAdditionalInfo

data class VacationUiModel(
    val id: Int,
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
    val time: String,
    val duration: String
)