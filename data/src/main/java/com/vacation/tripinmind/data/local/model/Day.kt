package com.vacation.tripinmind.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class Day(
    val nameDay: String = "",
    val additionalInfo: String = "",
    val activity: List<Activity> = listOf()
)