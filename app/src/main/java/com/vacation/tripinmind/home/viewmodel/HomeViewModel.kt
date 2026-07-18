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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vacationRepository: VacationRepository,
    private val userProfileRepository: UserProfileRepository,
    private val firebaseAuth: FirebaseAuth,
    private val crashlytics: FirebaseCrashlytics
) : ViewModel() {

    private val _internalState = MutableStateFlow(VacationUiViewState())

    private val _isLoggedIn = MutableStateFlow(firebaseAuth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _isLoggedIn.value = auth.currentUser != null
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    val vacationState: StateFlow<VacationUiViewState> = combine(
        vacationRepository.getAllItems(),
        vacationRepository.getSharedVacationsFlow(),
        _internalState
    ) { vacations, sharedVacations, internalState ->
        internalState.copy(
            isLoading = false,
            vacations = vacations,
            sharedVacations = sharedVacations
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VacationUiViewState(isLoading = true)
    )

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
        _internalState.update { it.copy(selectedFilter = selectedFilter) }
    }

    fun handleIntent(vacationIntent: VacationIntent) {
        when (vacationIntent) {
            is VacationIntent.DeleteVacation -> deleteVacation(vacationIntent.vacationDto)
            is VacationIntent.ArchiveVacation -> archiveVacation(vacationIntent.vacationDto)
            is VacationIntent.ToggleShowVacationFilter -> toggleShowArchived(vacationIntent.vacationFilter)
            is VacationIntent.CreateShareCode -> createShareCode()
        }
    }

    private fun createShareCode() {
        viewModelScope.launch {
            val shareCode = userProfileRepository.insertItem()

            shareCode?.let { code ->
                _internalState.update {
                    it.copy(
                        shareCode = code
                    )
                }

                // Init Crashlytics
                firebaseAuth.uid?.let {
                    crashlytics.setUserId(it)
                }
            }
        }
    }

    override fun onCleared() {
        firebaseAuth.removeAuthStateListener(authStateListener)
        super.onCleared()
    }
}
