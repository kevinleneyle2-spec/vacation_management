package com.vacation.tripinmind.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vacation.tripinmind.data.local.model.VacationDto
import com.vacation.tripinmind.data.repository.UserProfileRepository
import com.vacation.tripinmind.data.repository.VacationRepository
import com.vacation.tripinmind.home.model.VacationFilter
import com.vacation.tripinmind.home.intent.VacationIntent
import com.vacation.tripinmind.home.model.VacationUiViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vacationRepository: VacationRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {
    private val _vacationState = MutableStateFlow(VacationUiViewState())
    val vacationState: StateFlow<VacationUiViewState> = _vacationState


    init {
        handleIntent(VacationIntent.LoadData)
    }

    private fun findAllVacation() {
        viewModelScope.launch {
            vacationRepository.getAllItems().collect { vacations ->
                _vacationState.update {
                    it.copy(
                        isLoading = false,
                        vacations = vacations
                    )
                }
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

    private fun toggleShowArchived(selectedFilter: VacationFilter) {
        _vacationState.update { it.copy(selectedFilter = selectedFilter) }
    }

    fun handleIntent(vacationIntent: VacationIntent) {
        when (vacationIntent) {
            is VacationIntent.LoadData -> loadData()
            is VacationIntent.DeleteVacation -> deleteVacation(vacationIntent.vacationDto)
            is VacationIntent.ArchiveVacation -> archiveVacation(vacationIntent.vacationDto)
            is VacationIntent.ToggleShowVacationFilter -> toggleShowArchived(vacationIntent.vacationFilter)
            is VacationIntent.CreateShareCode -> createShareCode()
        }
    }

    private fun loadData() {
        _vacationState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            findAllVacation()
        }
    }

    private fun createShareCode() {
        viewModelScope.launch {
            val shareCode = userProfileRepository.insertItem()

            shareCode?.let { code ->
                _vacationState.update {
                    it.copy(
                        shareCode = code
                    )
                }

                // Init Crashlytics
                FirebaseAuth.getInstance().uid?.let {
                    FirebaseCrashlytics.getInstance().setUserId(it)
                }

                startListeningSharedVacations()
            }
        }
    }

    private fun startListeningSharedVacations() {
        viewModelScope.launch {
            vacationRepository.getSharedVacationsFlow().collect { sharedList ->
                _vacationState.update { it.copy(sharedVacations = sharedList) }
            }
        }
    }
}