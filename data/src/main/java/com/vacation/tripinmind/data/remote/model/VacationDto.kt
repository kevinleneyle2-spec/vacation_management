package com.vacation.tripinmind.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class VacationDto(
    val id: String = "",
    val name: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val days: List<DayDto> = listOf(),
    val ideas: List<String> = listOf(),
    val image: String = "",
    val isArchived: Boolean = false,
    val createdBy: String = "",
    val shareWith: List<String> = listOf(),
    val shareWithUid: List<String> = listOf()
)