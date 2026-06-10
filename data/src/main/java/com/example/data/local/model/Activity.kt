package com.example.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class Activity(
    val activityName: String,
    val activityTime: String,
    val activityDuration: String,
    val activityLocation: String = ""
)
