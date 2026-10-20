package com.vacation.tripinmind.home.model

import com.vacation.tripinmind.model.Vacation

data class VacationUiViewState(
    val isLoading: Boolean = false,
    val vacations: List<Vacation> = emptyList(),
    val sharedVacations: List<Vacation> = emptyList(),
    val selectedFilter: VacationFilter = VacationFilter.PROJECTS,
    val shareCode: String = ""
)