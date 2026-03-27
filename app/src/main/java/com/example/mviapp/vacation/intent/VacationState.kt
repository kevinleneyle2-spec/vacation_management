package com.example.mviapp.vacation.intent

import com.example.data.local.model.Day
import com.example.data.local.model.VacationDto

data class VacationState(
    val id: Int = 0,
    val vacationName: String = "",
    val numDays: Int = 0,
    val days: List<Day> = emptyList(),
) {
    fun toVacationDto() = VacationDto(
        id = id,
        name = vacationName,
        nbrDay = numDays,
        days = days
    )
}