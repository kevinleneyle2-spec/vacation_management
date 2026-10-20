package com.vacation.tripinmind.vacation.intent

import com.vacation.tripinmind.model.Activity
import com.vacation.tripinmind.model.Vacation

sealed class InitIntent {
    data class UpdateName(val name: String) : InitIntent()

    data class UpdateStartDate(val date: Long) : InitIntent()
    data class UpdateEndDate(val date: Long) : InitIntent()

    data class UpdateAddInfo(val index: Int, val additionalInfo: String) : InitIntent()

    data class AddDayActivities(val index: Int, val activity: Activity) : InitIntent()

    data class UpdateDayActivities(val dayNumber: Int, val index: Int, val activity: Activity) :
        InitIntent()

    data class RemoveDayActivities(val dayNumber: Int, val index: Int) : InitIntent()

    data class AddIdea(val idea: String) : InitIntent()

    data class UpdateImage(val image: String) : InitIntent()

    data class RemoveIdea(val index: Int) : InitIntent()

    data class CreateVacation(val vacation: Vacation) : InitIntent()

    data class UpdateVacation(val vacation: Vacation) : InitIntent()

    data class LoadVacation(val id: String) : InitIntent()
}