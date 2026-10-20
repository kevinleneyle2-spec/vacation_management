package com.vacation.tripinmind.home.intent

import com.vacation.tripinmind.home.model.VacationFilter
import com.vacation.tripinmind.model.Vacation

sealed class VacationIntent {
    data class DeleteVacation(val vacationDto: Vacation) : VacationIntent()
    data class ArchiveVacation(val vacationDto: Vacation) : VacationIntent()
    data class ToggleShowVacationFilter(val vacationFilter: VacationFilter) : VacationIntent()

    object CreateShareCode : VacationIntent()
}