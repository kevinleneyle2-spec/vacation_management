package com.vacation.tripinmind.home.model

import com.vacation.tripinmind.data.local.model.VacationDto
import com.vacation.tripinmind.home.model.VacationFilter

data class VacationUiViewState(
    val isLoading: Boolean = false,
    val vacations: List<VacationDto> = emptyList(),
    val sharedVacations: List<VacationDto> = emptyList(),
    val selectedFilter: VacationFilter = VacationFilter.PROJECTS,
    val shareCode: String = ""
)