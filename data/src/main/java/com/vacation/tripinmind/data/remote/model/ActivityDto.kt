package com.vacation.tripinmind.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ActivityDto(
    val activityName: String = "",
    val activityTime: String = "",
    val activityDuration: String = "",
    val activityLocation: String = ""
)