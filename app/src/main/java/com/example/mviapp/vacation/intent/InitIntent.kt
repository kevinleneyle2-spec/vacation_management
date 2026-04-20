package com.example.mviapp.vacation.intent

import android.hardware.SensorAdditionalInfo
import com.example.data.local.model.Activity
import com.example.data.local.model.VacationDto

sealed class InitIntent {
    data class UpdateName(val name: String) : InitIntent()

    data class UpdateStartDate(val date: String) : InitIntent()
    data class UpdateDays(val days: String) : InitIntent()

    data class UpdateAddInfo(val index: Int, val additionalInfo: String) : InitIntent()

    data class AddDayActivities(val index: Int, val activity: Activity) : InitIntent()

    data class RemoveDayActivities(val dayNumber: Int, val index: Int) : InitIntent()

    data class AddIdea(val idea: String) : InitIntent()

    data class UpdateImage(val image: String) : InitIntent()

    data class RemoveIdea(val index: Int) : InitIntent()

    data class  CreateVacation(val vacationDto: VacationDto) : InitIntent()

    data class  UpdateVacation(val vacationDto: VacationDto) : InitIntent()

    data class LoadVacation(val id: Int) : InitIntent()
}