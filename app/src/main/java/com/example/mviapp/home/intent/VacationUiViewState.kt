package com.example.mviapp.home.intent

import com.example.data.local.model.VacationDto

data class VacationUiViewState(
    val isLoading: Boolean = false,
    val vacations: List<VacationDto> = emptyList()
)