package com.vacation.tripinmind.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vacation.tripinmind.home.intent.VacationIntent
import com.vacation.tripinmind.home.model.VacationFilter
import com.vacation.tripinmind.home.model.VacationUiViewState
import com.vacation.tripinmind.model.Vacation
import com.vacation.tripinmind.mviapp.util.UiText
import com.vacation.tripinmind.repository.UserProfileRepository
import com.vacation.tripinmind.repository.VacationRepository
import com.vacation.tripinmind.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val _homeUiEvent = Channel<UiEvent>()
    val homeUiEvent = _homeUiEvent.receiveAsFlow()

    private val _isLoggedIn = MutableStateFlow(firebaseAuth.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _isLoggedIn.value = auth.currentUser != null
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    val vacationState: StateFlow<VacationUiViewState> = combine(
        vacationRepository.observeVacations(),
        vacationRepository.getSharedVacationsFlow(),
        _internalState
    ) { vacations, sharedVacations, internalState ->
        internalState.copy(
            isLoading = false,
            vacations = vacations,
            sharedVacations = sharedVacations
        )
    }.catch { e ->
        emit(
            _internalState.value.copy(
                isLoading = false
            )
        )
        e.message?.let {
            _homeUiEvent.send(UiEvent.ShowSnackbar(UiText.DynamicString(it)))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VacationUiViewState(isLoading = true)
    )

    private fun deleteVacation(vacationDto: Vacation) {
        viewModelScope.launch {

            vacationRepository.deleteItem(vacationDto)
                .onFailure { e ->
                    e.message?.let {
                        _homeUiEvent.send(UiEvent.ShowSnackbar(UiText.DynamicString(it)))
                    }
                }
        }
    }

    private fun archiveVacation(vacationDto: Vacation) {
        viewModelScope.launch {
            vacationRepository.updateItem(vacationDto.copy(isArchived = !vacationDto.isArchived))
                .onFailure {
                    it.message?.let { message ->
                        _homeUiEvent.send(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
                    }
                }
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
            userProfileRepository.insertItem()
                .onSuccess { shareCode ->
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
                .onFailure {
                    it.message?.let { message ->
                        _homeUiEvent.send(UiEvent.ShowSnackbar(UiText.DynamicString(message)))
                    }
                }

        }
    }

    override fun onCleared() {
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}
