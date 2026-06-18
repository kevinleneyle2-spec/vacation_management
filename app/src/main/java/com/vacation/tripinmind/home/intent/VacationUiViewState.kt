package com.vacation.tripinmind.home.intent

import com.vacation.tripinmind.data.local.model.VacationDto

data class VacationUiViewState(
    val isLoading: Boolean = false,
    val vacations: List<VacationDto> = emptyList(),
    val showArchived: Boolean = false,
    val shareCode: String = ""
)