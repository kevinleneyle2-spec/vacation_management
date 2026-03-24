package com.example.data.local.model

import kotlinx.serialization.Serializable
import java.sql.Time

@Serializable
data class Activity(
    val activityName: String,
    val activityTime: String
)