package com.vacation.tripinmind.details.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.vacation.tripinmind.details.intent.DetailsIntent
import com.vacation.tripinmind.details.model.DetailsError
import com.vacation.tripinmind.details.model.VacationUiModel
import com.vacation.tripinmind.model.Vacation
import com.vacation.tripinmind.repository.UserProfileRepository
import com.vacation.tripinmind.repository.VacationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val vacationRepository: VacationRepository,
    private val userProfileRepository: UserProfileRepository,
    private val firebaseAuth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val vacationId: String? = savedStateHandle.get<String>("vacationId")
    private val isSharedVacation: Boolean = savedStateHandle["sharedVacation"] ?: false
    private val _error = MutableStateFlow<DetailsError?>(null)

    init {
        handleIntent(DetailsIntent.LoadData)
    }

    val vacation: StateFlow<VacationUiModel?> = vacationId?.let { id ->
        combine(
            vacationRepository.getVacationById(id, isSharedVacation),
            _error
        ) { dto, error ->
            dto?.let {
                VacationUiModel(
                    vacation = it,
                    error = error,
                    isSharedVacation = isSharedVacation
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    } ?: MutableStateFlow(null)

    fun handleIntent(detailsIntent: DetailsIntent) {
        when (detailsIntent) {
            is DetailsIntent.AddViewer -> {
                addViewer(detailsIntent.vacation, detailsIntent.shareCode)
            }

            is DetailsIntent.ClearError -> {
                _error.value = null
            }

            is DetailsIntent.RemoveViewer -> {
                removeViewer(detailsIntent.index)
            }

            else -> {}
        }
    }

    private fun removeViewer(index: Int) {
        viewModelScope.launch {
            val vacation = vacation.value?.vacation

            vacation?.let { currentVacation ->
                val newShareWith = currentVacation.shareWith.toMutableList().apply {
                    if (index in indices) removeAt(index)
                }.toList()
                val newShareWithUid = currentVacation.shareWithUid.toMutableList().apply {
                    if (index in indices) removeAt(index)
                }.toList()

                vacationRepository.updateItem(
                    currentVacation.copy(
                        shareWith = newShareWith,
                        shareWithUid = newShareWithUid
                    )
                ).onFailure {
                    it.message?.let { message ->
                        _error.value = DetailsError.NOT_FOUND
                    }
                }
            }
        }
    }

    private fun addViewer(vacation: Vacation?, shareCode: String?) {
        _error.value = null

        viewModelScope.launch {
            shareCode?.let { userShareCode ->
                val formattedCode = userShareCode.chunked(3).joinToString("-")
                userProfileRepository.getItemByCode(formattedCode)
                    .onSuccess { userProfile ->
                        if (userProfile?.uuid == firebaseAuth.uid) {
                            _error.value = DetailsError.NOT_YOURSELF
                            return@launch
                        }

                        if (vacation != null && userProfile != null) {
                            val shareWithAlreadyAdded =
                                vacation.shareWith.any { it == userProfile.shareCode }

                            if (vacation.shareWith.size >= 5) {
                                _error.value = DetailsError.TOO_MANY_VIEWERS
                                return@launch
                            }

                            if (shareWithAlreadyAdded) {
                                _error.value = DetailsError.ALREADY_ADDED
                            } else {
                                val shareWith = vacation.shareWith + userProfile.shareCode
                                val shareWithUid = vacation.shareWithUid + userProfile.uuid

                                vacationRepository.updateItem(
                                    vacation.copy(
                                        shareWith = shareWith,
                                        shareWithUid = shareWithUid
                                    )
                                ).onFailure {
                                    it.message?.let { message ->
                                        _error.value = DetailsError.NOT_FOUND
                                    }
                                }.onSuccess {
                                    _error.value = DetailsError.SUCCESS
                                }
                            }
                        } else {
                            _error.value = DetailsError.NOT_FOUND
                        }
                    }
                    .onFailure {
                        _error.value = DetailsError.NOT_FOUND
                    }
            }
        }
    }
}
