package com.example.mviapp.vacation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.model.Day
import com.example.data.local.model.VacationDto
import com.example.data.repository.VacationRepository
import com.example.mviapp.vacation.intent.InitIntent
import com.example.mviapp.vacation.intent.VacationState
import com.example.mviapp.vacation.ui.InitViewModelActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class InitViewModel @Inject constructor(
    private val vacationRepository: VacationRepository
) : ViewModel(), InitViewModelActions {

    private val _initState = MutableStateFlow(VacationState())
    override val initState: StateFlow<VacationState> = _initState

    private val _initValidation = MutableStateFlow(false)
    override val initValidation: StateFlow<Boolean> = _initValidation

    private val inputSdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    private val outputSdf = SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private fun createVacation(vacationDto: VacationDto) {
        viewModelScope.launch {
            vacationRepository.insertItem(vacationDto)
        }
    }

    private fun updateVacation(vacationDto: VacationDto) {
        viewModelScope.launch {
            vacationRepository.updateItem(vacationDto)
        }
    }

    private fun updateValidation() {
        val state = _initState.value
        val isValid = state.vacationName.isNotBlank() && state.numDays > 0 && state.startDate.isNotBlank()
        _initValidation.value = isValid
    }

    private fun calculateDays(startDate: String, count: Int, currentDays: List<Day>): List<Day> {
        val baseDate = try {
            if (startDate.isNotBlank()) inputSdf.parse(startDate) else null
        } catch (e: Exception) {
            null
        }

        return List(count) { index ->
            val dayName = if (baseDate != null) {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.time = baseDate
                calendar.add(Calendar.DAY_OF_YEAR, index)
                outputSdf.format(calendar.time).replaceFirstChar { it.uppercase() }
            } else {
                currentDays.getOrNull(index)?.nameDay ?: ""
            }

            val existingDay = currentDays.getOrNull(index)
            Day(
                nameDay = dayName,
                additionalInfo = existingDay?.additionalInfo ?: "",
                activity = existingDay?.activity ?: emptyList()
            )
        }
    }

    override fun handleIntent(intent: InitIntent) {
        when (intent) {
            is InitIntent.UpdateName -> {
                _initState.update { it.copy(vacationName = intent.name) }
                updateValidation()
            }

            is InitIntent.UpdateStartDate -> {
                _initState.update { currentState ->
                    val newDays = calculateDays(intent.date, currentState.numDays, currentState.days)
                    currentState.copy(startDate = intent.date, days = newDays)
                }
                updateValidation()
            }

            is InitIntent.UpdateDays -> {
                val input = intent.days
                if (input.isEmpty()) {
                    _initState.update { it.copy(numDays = 0, days = emptyList()) }
                } else {
                    val number = input.toIntOrNull()
                    if (number != null && number in 1..15) {
                        _initState.update { currentState ->
                            val newDays = calculateDays(currentState.startDate, number, currentState.days)
                            currentState.copy(numDays = number, days = newDays)
                        }
                    }
                }
                updateValidation()
            }

            is InitIntent.UpdateAddInfo -> {
                val currentDays = _initState.value.days.toMutableList()
                if (intent.index in currentDays.indices) {
                    currentDays[intent.index] = currentDays[intent.index].copy(additionalInfo = intent.additionalInfo)
                    _initState.update { it.copy(days = currentDays) }
                }
            }

            is InitIntent.AddDayActivities -> {
                val currentDays = _initState.value.days.toMutableList()
                if (intent.index in currentDays.indices) {
                    val updatedDay = currentDays[intent.index].copy(
                        activity = currentDays[intent.index].activity + intent.activity
                    )
                    currentDays[intent.index] = updatedDay
                    _initState.update { it.copy(days = currentDays) }
                }
            }

            is InitIntent.RemoveDayActivities -> {
                val currentDays = _initState.value.days.toMutableList()
                if (intent.dayNumber in currentDays.indices) {
                    val day = currentDays[intent.dayNumber]
                    val activities = day.activity.toMutableList()
                    if (intent.index in activities.indices) {
                        activities.removeAt(intent.index)
                        currentDays[intent.dayNumber] = day.copy(activity = activities)
                        _initState.update { it.copy(days = currentDays) }
                    }
                }
            }

            is InitIntent.AddIdea -> {
                val currentIdeas = _initState.value.ideas.toMutableList()
                currentIdeas.add(intent.idea)
                _initState.update { it.copy(ideas = currentIdeas) }
            }

            is InitIntent.RemoveIdea -> {
                val currentIdeas = _initState.value.ideas.toMutableList()
                currentIdeas.removeAt(intent.index)
                _initState.update { it.copy(ideas = currentIdeas) }
            }

            is InitIntent.UpdateImage -> {
                _initState.update { it.copy(image = intent.image) }
            }

            is InitIntent.CreateVacation -> createVacation(intent.vacationDto)
            is InitIntent.UpdateVacation -> updateVacation(intent.vacationDto)

            is InitIntent.LoadVacation -> {
                viewModelScope.launch {
                    vacationRepository.getItemById(intent.id).collect { vacation ->
                        vacation?.let {
                            _initState.update { currentState ->
                                currentState.copy(
                                    id = it.id,
                                    vacationName = it.name,
                                    startDate = it.startDate,
                                    numDays = it.nbrDay,
                                    days = it.days,
                                    ideas = it.ideas,
                                    image = it.image,
                                    isArchived = it.isArchived
                                )
                            }
                            updateValidation()
                        }
                    }
                }
            }
        }
    }
}