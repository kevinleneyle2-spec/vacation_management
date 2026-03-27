package com.example.mviapp.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.VacationRepository
import com.example.mviapp.details.model.ActivityUiModel
import com.example.mviapp.details.model.DayUiModel
import com.example.mviapp.details.model.VacationUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val vacationRepository: VacationRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vacationId: Int? = savedStateHandle.get<String>("vacationId")?.toIntOrNull()

    val vacation: StateFlow<VacationUiModel?> = vacationId?.let { id ->
        vacationRepository.getItemById(id)
            .map { dto ->
                VacationUiModel(
                    id = dto.id,
                    name = dto.name,
                    days = dto.days.map { day ->
                        DayUiModel(
                            name = day.nameDay,
                            activities = day.activity.map { activity ->
                                ActivityUiModel(
                                    name = activity.activityName,
                                    time = activity.activityTime,
                                    duration = activity.activityDuration
                                )
                            }
                        )
                    }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    } ?: kotlinx.coroutines.flow.MutableStateFlow(null)
}