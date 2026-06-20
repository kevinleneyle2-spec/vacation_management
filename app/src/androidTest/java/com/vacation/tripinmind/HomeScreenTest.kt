package com.vacation.tripinmind

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.vacation.tripinmind.data.local.model.VacationDto
import com.vacation.tripinmind.home.intent.VacationUiViewState
import com.vacation.tripinmind.home.ui.HomeScreenContent
import com.vacation.tripinmind.ui.theme.MVIAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @Rule
    @JvmField
    var composeTestRule: ComposeContentTestRule = createComposeRule()

    private val fakeVacation = VacationDto(
        id = "1",
        name = "Paris",
        startDate = "10/05/2023",
        nbrDay = 3,
        days = emptyList(),
        ideas = emptyList(),
        image = "vacation_ico",
        isArchived = false,
        createdBy = "",
        shareWith = listOf()
    )

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.vacation.tripinmind", appContext.packageName)
    }

    @Test
    fun new_vacation_button_works() {
        var navigated = false
        composeTestRule.setContent {
            MVIAppTheme {
                HomeScreenContent(
                    vacationUiState = VacationUiViewState(),
                    onDeleteVacation = {}, onArchiveVacation = {},
                    onToggleShowFilterVacations = {},
                    onNavigate = { navigated = true }
                )
            }
        }

        composeTestRule.onNodeWithTag("newVacationButton").performClick()

        assert(navigated)
    }

    @Test
    fun vacation_details_click_works() {
        var clickedId = -1L
        composeTestRule.setContent {
            MVIAppTheme {
                HomeScreenContent(
                    vacationUiState = VacationUiViewState(vacations = listOf(fakeVacation)),
                    onDeleteVacation = {}, onArchiveVacation = {},
                    onToggleShowFilterVacations = {},
                    onNavigate = { route ->
                        if (route.contains("1")) clickedId = 1
                    }
                )
            }
        }

        composeTestRule.onNodeWithTag("vacationCard").performClick()

        assertEquals(1L, clickedId)
    }

    @Test
    fun vacation_archived_button_works() {
        val uiState = mutableStateOf(
            VacationUiViewState(vacations = listOf(fakeVacation))
        )

        composeTestRule.setContent {
            MVIAppTheme {
                HomeScreenContent(
                    vacationUiState = uiState.value,
                    onDeleteVacation = {},
                    onArchiveVacation = { clickedVacation ->
                        val updatedList = uiState.value.vacations.map {
                            if (it.id == clickedVacation.id) it.copy(isArchived = !it.isArchived)
                            else it
                        }
                        uiState.value = uiState.value.copy(vacations = updatedList)
                    },
                    onToggleShowFilterVacations = { selectFilter ->
                        uiState.value = uiState.value.copy(selectedFilter = selectFilter)
                    },
                    onNavigate = {}
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("vacationCard").fetchSemanticsNodes().isNotEmpty()
        }

        val firstCard = composeTestRule.onAllNodesWithTag("vacationCard")[0]
        firstCard.assertIsDisplayed()

        composeTestRule.onNodeWithTag("archivedVacationButton").performClick()

        composeTestRule.onNodeWithText("No vacation planned").assertIsDisplayed()

        composeTestRule.onNodeWithTag("archivedVacationViewButton").performClick()

        composeTestRule.onNodeWithText("No vacation planned").assertIsNotDisplayed()
        firstCard.assertIsDisplayed()

        composeTestRule.onNodeWithTag("archivedVacationButton").performClick()

        composeTestRule.onNodeWithText("No vacations archived").assertIsDisplayed()

        composeTestRule.onNodeWithTag("vacationViewButton").performClick()

        firstCard.assertIsDisplayed()
    }

    @Test
    fun vacation_shared_button_works() {
        val uiState = mutableStateOf(
            VacationUiViewState(vacations = listOf(fakeVacation))
        )

        composeTestRule.setContent {
            MVIAppTheme {
                HomeScreenContent(
                    vacationUiState = uiState.value,
                    onDeleteVacation = {},
                    onArchiveVacation = { clickedVacation ->
                        val updatedList = uiState.value.vacations.map {
                            if (it.id == clickedVacation.id) it.copy(isArchived = !it.isArchived)
                            else it
                        }
                        uiState.value = uiState.value.copy(vacations = updatedList)
                    },
                    onToggleShowFilterVacations = { selectFilter ->
                        uiState.value = uiState.value.copy(selectedFilter = selectFilter)
                    },
                    onNavigate = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("sharedVacationViewButton").performClick()

        composeTestRule.onNodeWithText("No vacations shared").assertIsDisplayed()
    }

    @Test
    fun vacation_delete_button_works() {
        val uiState = mutableStateOf(
            VacationUiViewState(vacations = listOf(fakeVacation))
        )

        composeTestRule.setContent {
            MVIAppTheme {
                HomeScreenContent(
                    vacationUiState = uiState.value,
                    onDeleteVacation = { uiState.value = VacationUiViewState() },
                    onArchiveVacation = {},
                    onToggleShowFilterVacations = {},
                    onNavigate = {}
                )
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithTag("vacationCard").fetchSemanticsNodes().isNotEmpty()
        }

        val firstCard = composeTestRule.onAllNodesWithTag("vacationCard")[0]
        firstCard.assertIsDisplayed()

        composeTestRule.onNodeWithTag("vacationDeleteButton").performClick()
        composeTestRule.onNodeWithText("No vacation planned").assertIsDisplayed()
        firstCard.assertDoesNotExist()
    }
}