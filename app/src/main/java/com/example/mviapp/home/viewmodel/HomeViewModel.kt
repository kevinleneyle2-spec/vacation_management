package com.example.mviapp.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.model.VacationDto
import com.example.data.repository.VacationRepository
import com.example.mviapp.home.intent.VacationIntent
import com.example.mviapp.home.intent.VacationUiViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vacationRepository: VacationRepository
) : ViewModel() {
    private val _vacationState = MutableStateFlow(VacationUiViewState())
    val vacationState: StateFlow<VacationUiViewState> = _vacationState


    init {
        handleIntent(VacationIntent.LoadData)
    }

    private fun findAllVacation() {
        viewModelScope.launch {
            vacationRepository.getAllItems().collect { vacations ->
                _vacationState.update { it.copy(
                    isLoading = false,
                    vacations = vacations
                ) }
            }
        }
    }

    private fun deleteVacation(vacationDto: VacationDto) {
        viewModelScope.launch {
            vacationRepository.deleteItem(vacationDto)
        }
    }

    private fun archiveVacation(vacationDto: VacationDto) {
        viewModelScope.launch {
            vacationRepository.updateItem(vacationDto.copy(isArchived = !vacationDto.isArchived))
        }
    }

    private fun toggleShowArchived() {
        _vacationState.update { it.copy(showArchived = !it.showArchived) }
    }

    fun handleIntent(vacationIntent: VacationIntent, onNavigate: (String) -> Unit = {}) {
        when (vacationIntent) {
            is VacationIntent.LoadData -> loadData()
            is VacationIntent.DeleteVacation -> deleteVacation(vacationIntent.vacationDto)
            is VacationIntent.ArchiveVacation -> archiveVacation(vacationIntent.vacationDto)
            is VacationIntent.ToggleShowArchived -> toggleShowArchived()
        }
    }

    private fun loadData() {
        _vacationState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            findAllVacation()
        }
    }
}