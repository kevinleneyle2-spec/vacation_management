package com.example.mviapp.home.intent

import com.example.data.local.model.VacationDto

sealed class VacationIntent {
    object LoadData : VacationIntent()
    data class DeleteVacation(val vacationDto: VacationDto) : VacationIntent()
}