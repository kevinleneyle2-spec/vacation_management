package com.vacation.tripinmind.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfileDto(
    val uuid: String = "",
    val shareCode: String = ""
)