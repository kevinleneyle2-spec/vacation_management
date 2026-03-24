package com.example.mviapp.vacation.intent

import com.example.data.local.model.Activity
import com.example.data.local.model.VacationDto

sealed class InitIntent {
    data class UpdateName(val name: String) : InitIntent()
    data class UpdateDays(val days: String) : InitIntent()

    data class UpdateDayName(val index: Int, val name: String) : InitIntent()

    data class AddDayActivities(val index: Int, val activity: Activity) : InitIntent()

    data class  CreateVacation(val vacationDto: VacationDto) : InitIntent()
}