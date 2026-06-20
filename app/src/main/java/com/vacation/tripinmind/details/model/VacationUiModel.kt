package com.vacation.tripinmind.details.model

import com.vacation.tripinmind.data.local.model.VacationDto

enum class DetailsError {
    NOT_FOUND,
    ALREADY_ADDED,
    NOT_YOURSELF,
    TOO_MANY_VIEWERS,
    SUCCESS
}

data class VacationUiModel(
    val vacation: VacationDto,
    val error: DetailsError? = null,
    val isSharedVacation: Boolean = false
)
