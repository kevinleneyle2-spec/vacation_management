package com.vacation.tripinmind.model

data class Vacation(
    val id: String = "",
    val name: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val days: List<Day> = listOf(),
    val ideas: List<String> = listOf(),
    val image: String = "",
    val isArchived: Boolean = false,
    val createdBy: String = "",
    val shareWith: List<String> = listOf(),
    val shareWithUid: List<String> = listOf()
)