package com.vacation.tripinmind.vacation.intent

import com.vacation.tripinmind.data.local.model.Day
import com.vacation.tripinmind.data.local.model.VacationDto
import com.vacation.tripinmind.mviapp.util.UiText

data class VacationState(
    val id: String = "",
    val vacationName: String = "",
    val startDate: String = "",
    val numDays: Int = 0,
    val days: List<Day> = emptyList(),
    val ideas: List<String> = emptyList(),
    val image: String = "vacation_ico",
    val isArchived: Boolean = false,
    val createdBy: String = "",
    val shareWith: List<String> = listOf(),
    val shareWithUid: List<String> = listOf(),
    val errorMessage: UiText? = null
) {
    fun toVacationDto() = VacationDto(
        id = id,
        name = vacationName,
        startDate = startDate,
        nbrDay = numDays,
        days = days,
        ideas = ideas,
        image = image,
        isArchived = isArchived,
        createdBy = createdBy,
        shareWith = shareWith,
        shareWithUid = shareWithUid
    )
}