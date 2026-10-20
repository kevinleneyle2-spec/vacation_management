package com.vacation.tripinmind.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class DayEntity(
    val date: Long,
    val nameDay: String = "",
    val additionalInfo: String = "",
    val activity: List<ActivityEntity> = listOf()
)