package com.vacation.tripinmind.details.intent

import com.vacation.tripinmind.data.local.model.VacationDto

sealed class DetailsIntent {
    object LoadData : DetailsIntent()
    data class AddViewer(val vacationDto: VacationDto?, val shareCode: String? = null) : DetailsIntent()

    data class RemoveViewer(val index: Int) : DetailsIntent()
    object ClearError : DetailsIntent()
}