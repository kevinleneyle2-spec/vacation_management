package com.example.data.local.model

import kotlinx.serialization.Serializable

@Serializable
data class Day(
    val nameDay: String,
    val activity: List<Activity>
)