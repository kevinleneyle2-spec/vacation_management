package com.vacation.tripinmind.navigation

object AppDestinations {
    const val HOME_ROUTE = "home"

    const val INIT_ROUTE = "init"

    const val ACTIVITIES_ROUTE = "activities"

    const val DETAILS_ROUTE = "details/{vacationId}"
    fun buildDetailsRoute(vacationId: Int) = "details/$vacationId"

    fun buildEditRoute(vacationId: Int) = "activities/$vacationId"
}