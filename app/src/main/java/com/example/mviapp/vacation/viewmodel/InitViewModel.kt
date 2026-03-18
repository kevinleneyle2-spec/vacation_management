package com.example.mviapp.vacation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.model.Day
import com.example.data.local.model.VacationDto
import com.example.data.repository.VacationRepository
import com.example.mviapp.vacation.intent.InitIntent
import com.example.mviapp.vacation.intent.VacationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitViewModel @Inject constructor(
    private val vacationRepository: VacationRepository
) : ViewModel() {

    private val _initState = MutableStateFlow(VacationState())
    val initState: StateFlow<VacationState> = _initState

    private val _initValidation = MutableStateFlow(false)
    val initValidation: StateFlow<Boolean> = _initValidation

    private fun createVacation(vacationDto: VacationDto) {
        viewModelScope.launch {
            vacationRepository.insertItem(vacationDto)
        }
    }

    private fun updateValidation() {
        val state = _initState.value

        val isValid = state.vacationName.isNotBlank() && state.numDays > 0

        _initValidation.value = isValid
    }

    fun handleIntent(intent: InitIntent) {
        when (intent) {
            is InitIntent.UpdateName -> {
                _initState.update { it.copy(vacationName = intent.name) }
                updateValidation()
            }

            is InitIntent.UpdateDays -> {
                val input = intent.days

                if (input.isEmpty()) {
                    _initState.update { it.copy(numDays = 0, days = emptyList()) }
                } else {
                    val number = input.toIntOrNull()

                    if (number != null && number in 0..15) {
                        _initState.update { currentState ->
                            val currentDays = currentState.days
                            val newDays = if (number > currentDays.size) {
                                currentDays + List(number - currentDays.size) { Day("", emptyList()) }
                            } else {
                                currentDays.take(number)
                            }
                            currentState.copy(numDays = number, days = newDays)
                        }
                    }
                }
                updateValidation()
            }

            is InitIntent.UpdateDayName -> {
                val currentDays = _initState.value.days.toMutableList()

                if (intent.index in currentDays.indices) {
                    currentDays[intent.index] = currentDays[intent.index].copy(nameDay = intent.name)
                    _initState.update { it.copy(days = currentDays) }
                }
            }

            is InitIntent.AddDayActivities -> {
                val currentDays = _initState.value.days.toMutableList()

                if (intent.index in currentDays.indices) {
                    val updatedDay = currentDays[intent.index].copy(
                        activity = currentDays[intent.index].activity + intent.activities
                    )
                    currentDays[intent.index] = updatedDay
                    _initState.update { it.copy(days = currentDays) }
                }
            }

            is InitIntent.CreateVacation -> createVacation(intent.vacationDto)
        }
    }
}
