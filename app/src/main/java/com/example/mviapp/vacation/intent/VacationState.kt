package com.example.mviapp.vacation.intent

import com.example.data.local.model.Day
import com.example.data.local.model.VacationDto

data class VacationState(
    val id: Int = 0,
    val vacationName: String = "",
    val startDate: String = "",
    val numDays: Int = 0,
    val days: List<Day> = emptyList(),
    val ideas: List<String> = emptyList(),
    val image: String = "vacation_ico",
    val isArchived: Boolean = false
) {
    fun toVacationDto() = VacationDto(
        id = id,
        name = vacationName,
        startDate = startDate,
        nbrDay = numDays,
        days = days,
        ideas = ideas,
        image = image,
        isArchived = isArchived
    )
}