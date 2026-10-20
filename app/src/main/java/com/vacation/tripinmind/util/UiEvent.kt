package com.vacation.tripinmind.util

import com.vacation.tripinmind.mviapp.util.UiText

sealed interface UiEvent {
    data class ShowSnackbar(val message: UiText) : UiEvent
}