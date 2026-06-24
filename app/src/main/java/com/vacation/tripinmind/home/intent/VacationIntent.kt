package com.vacation.tripinmind.home.intent

import com.vacation.tripinmind.data.local.model.VacationDto
import com.vacation.tripinmind.home.model.VacationFilter

sealed class VacationIntent {
    object LoadData : VacationIntent()
    data class DeleteVacation(val vacationDto: VacationDto) : VacationIntent()
    data class ArchiveVacation(val vacationDto: VacationDto) : VacationIntent()
    data class ToggleShowVacationFilter(val vacationFilter: VacationFilter) : VacationIntent()

    object CreateShareCode : VacationIntent()
}