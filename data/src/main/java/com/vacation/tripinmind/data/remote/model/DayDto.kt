package com.vacation.tripinmind.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class DayDto(
    val date: Long = 0L,
    val nameDay: String = "",
    val additionalInfo: String = "",
    val activity: List<ActivityDto> = listOf()
)