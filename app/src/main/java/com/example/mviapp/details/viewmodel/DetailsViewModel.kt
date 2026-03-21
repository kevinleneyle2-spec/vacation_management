package com.example.mviapp.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.data.repository.VacationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val vacationRepository: VacationRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val vacationId: Int? = savedStateHandle.get<String>("vacationId")?.toIntOrNull()
}