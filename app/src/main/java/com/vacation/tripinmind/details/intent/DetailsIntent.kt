package com.vacation.tripinmind.details.intent

import com.vacation.tripinmind.model.Vacation

sealed class DetailsIntent {
    object LoadData : DetailsIntent()
    data class AddViewer(val vacation: Vacation?, val shareCode: String? = null) : DetailsIntent()

    data class RemoveViewer(val index: Int) : DetailsIntent()
    object ClearError : DetailsIntent()
}