package com.vacation.tripinmind.model

data class Day(
    val date: Long,
    val nameDay: String = "",
    val additionalInfo: String = "",
    val activity: List<Activity> = listOf()
)