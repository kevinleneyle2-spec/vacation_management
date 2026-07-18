package com.vacation.tripinmind.vacation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vacation.tripinmind.data.local.model.Day
import com.vacation.tripinmind.data.local.model.VacationDto
import com.vacation.tripinmind.data.repository.VacationRepository
import com.vacation.tripinmind.mviapp.util.UiText
import com.google.firebase.auth.FirebaseAuth
import com.vacation.tripinmind.R
import com.vacation.tripinmind.vacation.intent.InitIntent
import com.vacation.tripinmind.vacation.model.VacationState
import com.vacation.tripinmind.vacation.ui.InitViewModelActions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class InitViewModel @Inject constructor(
    private val vacationRepository: VacationRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel(), InitViewModelActions {

    private val _initState = MutableStateFlow(VacationState())
    override val initState: StateFlow<VacationState> = _initState

    private val _initValidation = MutableStateFlow(false)
    override val initValidation: StateFlow<Boolean> = _initValidation

    private val outputSdf = SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private var loadVacationJob: Job? = null

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
        val isValid = state.vacationName.isNotBlank() &&
                state.startDate > 0L &&
                state.endDate >= state.startDate
        _initValidation.value = isValid
    }

    private fun calculateDays(startDate: Long, endDate: Long, currentDays: List<Day>): List<Day> {
        if (startDate <= 0L || endDate < startDate) return emptyList()

        val count = ((endDate - startDate) / (1000 * 60 * 60 * 24)).toInt() + 1
        val baseDate = Date(startDate)

        return List(count) { index ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                time = baseDate
                add(Calendar.DAY_OF_YEAR, index)
            }
            val dayName = outputSdf.format(calendar.time).replaceFirstChar { it.uppercase() }

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
                    val updatedEndDate = if (currentState.endDate > 0L && currentState.endDate < intent.date) {
                        intent.date
                    } else {
                        currentState.endDate
                    }
                    val newDays = calculateDays(intent.date, updatedEndDate, currentState.days)
                    currentState.copy(startDate = intent.date, endDate = updatedEndDate, days = newDays)
                }
                updateValidation()
            }

            is InitIntent.UpdateEndDate -> {
                _initState.update { currentState ->
                    val validEndDate = if (intent.date >= currentState.startDate) intent.date else currentState.startDate
                    val newDays = calculateDays(currentState.startDate, validEndDate, currentState.days)
                    currentState.copy(endDate = validEndDate, days = newDays)
                }
                updateValidation()
            }

            is InitIntent.UpdateAddInfo -> {
                val currentDays = _initState.value.days.toMutableList()
                if (intent.index in currentDays.indices) {
                    currentDays[intent.index] =
                        currentDays[intent.index].copy(additionalInfo = intent.additionalInfo)
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

            is InitIntent.UpdateDayActivities -> {
                val currentDays = _initState.value.days.toMutableList()
                if (intent.dayNumber in currentDays.indices) {
                    val day = currentDays[intent.dayNumber]
                    val activities = day.activity.toMutableList()
                    if (intent.index in activities.indices) {
                        activities.removeAt(intent.index)
                        activities.add(intent.index, intent.activity)
                        currentDays[intent.dayNumber] = day.copy(activity = activities)
                        _initState.update { it.copy(days = currentDays) }
                    }
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
                if (intent.index in currentIdeas.indices) {
                    currentIdeas.removeAt(intent.index)
                    _initState.update { it.copy(ideas = currentIdeas) }
                }
            }

            is InitIntent.UpdateImage -> {
                _initState.update { it.copy(image = intent.image) }
            }

            is InitIntent.CreateVacation -> {
                val uid = firebaseAuth.uid
                if (uid != null) {
                    _initState.update { it.copy(errorMessage = null) }
                    createVacation(intent.vacationDto.copy(createdBy = uid))
                } else {
                    _initState.update { it.copy(errorMessage = UiText.StringResource(R.string.common_error_auth_message)) }
                }
            }

            is InitIntent.UpdateVacation -> updateVacation(intent.vacationDto)

            is InitIntent.LoadVacation -> {
                loadVacationJob?.cancel()
                loadVacationJob = viewModelScope.launch {
                    vacationRepository.getItemById(intent.id).collect { vacation ->
                        vacation?.let {
                            _initState.update { currentState ->
                                currentState.copy(
                                    id = it.id,
                                    vacationName = it.name,
                                    startDate = it.startDate,
                                    endDate = it.endDate,
                                    days = it.days,
                                    ideas = it.ideas,
                                    image = it.image,
                                    isArchived = it.isArchived,
                                    createdBy = it.createdBy,
                                    shareWith = it.shareWith,
                                    shareWithUid = it.shareWithUid
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