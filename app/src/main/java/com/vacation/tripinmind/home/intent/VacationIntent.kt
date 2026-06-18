package com.vacation.tripinmind.home.intent

import com.vacation.tripinmind.data.local.model.VacationDto

sealed class VacationIntent {
    object LoadData : VacationIntent()
    data class DeleteVacation(val vacationDto: VacationDto) : VacationIntent()
    data class ArchiveVacation(val vacationDto: VacationDto) : VacationIntent()
    object ToggleShowArchived : VacationIntent()

    object CreateShareCode : VacationIntent()
}