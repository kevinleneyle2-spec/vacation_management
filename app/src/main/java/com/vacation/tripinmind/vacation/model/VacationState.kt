package com.vacation.tripinmind.vacation.model

import com.vacation.tripinmind.model.Day
import com.vacation.tripinmind.model.Vacation
import com.vacation.tripinmind.mviapp.util.UiText

data class VacationState(
    val id: String = "",
    val vacationName: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val days: List<Day> = emptyList(),
    val ideas: List<String> = emptyList(),
    val image: String = "vacation_ico",
    val isArchived: Boolean = false,
    val createdBy: String = "",
    val shareWith: List<String> = listOf(),
    val shareWithUid: List<String> = listOf()
) {
    val numDays: Int
        get() = if (startDate > 0L && endDate >= startDate) {
            ((endDate - startDate) / (1000 * 60 * 60 * 24)).toInt() + 1
        } else {
            days.size
        }

    fun toVacation() = Vacation(
        id = id,
        name = vacationName,
        startDate = startDate,
        endDate = endDate,
        days = days,
        ideas = ideas,
        image = image,
        isArchived = isArchived,
        createdBy = createdBy,
        shareWith = shareWith,
        shareWithUid = shareWithUid
    )
}