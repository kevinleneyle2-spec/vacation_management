package com.example.mviapp

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mviapp.home.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class ActivityScreenTest {
    @Rule
    @JvmField
    var composeTestRule: ComposeContentTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun activities_completed_fields_work() {
        val date = LocalDate.now().plusDays(10)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)

        val formattedDate = date.format(formatter)

        composeTestRule.onNodeWithTag("newVacationButton").performClick()

        composeTestRule.onNodeWithText("Your vacation").assertIsDisplayed()

        composeTestRule.onNodeWithTag("initNameTextField").performTextInput("Paris")
        composeTestRule.onNodeWithTag("initDateTextField").performClick()
        composeTestRule.onNodeWithText(formattedDate).performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.onNodeWithTag("initNumDayTextField").performTextInput("5")

        composeTestRule.onNodeWithTag("initNextButton").performClick()

        composeTestRule.onNodeWithText("What have you planned?").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("activityAdditionalInfoTextField")[0].performTextInput("with friends...")

        composeTestRule.onAllNodesWithTag("activitiesAddButton")[0].performClick()

        composeTestRule.onNodeWithTag("activityNameTextField")
            .performTextInput("Visit the Eiffel Tower")
        composeTestRule.onNodeWithTag("activityAddressTextField").performTextInput("Paris")

        composeTestRule.onNodeWithTag("activityTimeTextField").performClick()
        composeTestRule.onNodeWithText("12h15").performClick()

        composeTestRule.onNodeWithTag("activityDurationTextField").performClick()
        composeTestRule.onNodeWithText("01h30").performClick()

        composeTestRule.onNodeWithText("ADD").performClick()

        composeTestRule.onNodeWithText("- 12h15 Visit the Eiffel Tower (01h30)").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("activityDeleteButton")[0].performClick()

        composeTestRule.onNodeWithTag("activityFinishButton").performClick()

        composeTestRule.onNodeWithText("PREPARE YOUR NEXT VACATION")
    }

    @Test
    fun activities_add_info_too_long() {
        val date = LocalDate.now().plusDays(10)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)
        val formattedDate = date.format(formatter)

        composeTestRule.onNodeWithTag("newVacationButton").performClick()

        composeTestRule.onNodeWithText("Your vacation").assertIsDisplayed()

        composeTestRule.onNodeWithTag("initNameTextField").performTextInput("Paris")
        composeTestRule.onNodeWithTag("initDateTextField").performClick()
        composeTestRule.onNodeWithText(formattedDate).performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.onNodeWithTag("initNumDayTextField").performTextInput("5")

        composeTestRule.onNodeWithTag("initNextButton").performClick()

        composeTestRule.onNodeWithText("What have you planned?").assertIsDisplayed()

        val longName = "A".repeat(110)
        composeTestRule.onAllNodesWithTag("activityAdditionalInfoTextField")[0].performTextInput(
            longName
        )

        composeTestRule.onAllNodesWithTag("activityAdditionalInfoTextField")[0].assert(hasText(""))
    }

    @Test
    fun activities_name_too_long() {
        val date = LocalDate.now().plusDays(10)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)
        val formattedDate = date.format(formatter)

        composeTestRule.onNodeWithTag("newVacationButton").performClick()

        composeTestRule.onNodeWithText("Your vacation").assertIsDisplayed()

        composeTestRule.onNodeWithTag("initNameTextField").performTextInput("Paris")
        composeTestRule.onNodeWithTag("initDateTextField").performClick()
        composeTestRule.onNodeWithText(formattedDate).performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.onNodeWithTag("initNumDayTextField").performTextInput("5")

        composeTestRule.onNodeWithTag("initNextButton").performClick()

        composeTestRule.onNodeWithText("What have you planned?").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("activitiesAddButton")[0].performClick()

        val longName = "A".repeat(30)
        composeTestRule.onNodeWithTag("activityNameTextField").performTextInput(longName)

        composeTestRule.onNodeWithTag("activityNameTextField").assert(hasText("A".repeat(25)))
    }

    @Test
    fun activities_address_too_long() {
        val date = LocalDate.now().plusDays(10)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)
        val formattedDate = date.format(formatter)

        composeTestRule.onNodeWithTag("newVacationButton").performClick()

        composeTestRule.onNodeWithText("Your vacation").assertIsDisplayed()

        composeTestRule.onNodeWithTag("initNameTextField").performTextInput("Paris")
        composeTestRule.onNodeWithTag("initDateTextField").performClick()
        composeTestRule.onNodeWithText(formattedDate).performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.onNodeWithTag("initNumDayTextField").performTextInput("5")

        composeTestRule.onNodeWithTag("initNextButton").performClick()

        composeTestRule.onNodeWithText("What have you planned?").assertIsDisplayed()

        composeTestRule.onAllNodesWithTag("activitiesAddButton")[0].performClick()

        val longName = "A".repeat(30)
        composeTestRule.onNodeWithTag("activityAddressTextField").performTextInput(longName)

        composeTestRule.onNodeWithTag("activityAddressTextField").assert(hasText("A".repeat(25)))
    }

    @Test
    fun activities_too_many_activities() {
        val date = LocalDate.now().plusDays(10)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)
        val formattedDate = date.format(formatter)

        composeTestRule.onNodeWithTag("newVacationButton").performClick()

        composeTestRule.onNodeWithText("Your vacation").assertIsDisplayed()

        composeTestRule.onNodeWithTag("initNameTextField").performTextInput("Paris")
        composeTestRule.onNodeWithTag("initDateTextField").performClick()
        composeTestRule.onNodeWithText(formattedDate).performClick()
        composeTestRule.onNodeWithText("OK").performClick()
        composeTestRule.onNodeWithTag("initNumDayTextField").performTextInput("5")

        composeTestRule.onNodeWithTag("initNextButton").performClick()

        composeTestRule.onNodeWithText("What have you planned?").assertIsDisplayed()

        (1..15).forEach { _ ->
            composeTestRule.onAllNodesWithTag("activitiesAddButton")[0].performClick()

            composeTestRule.onNodeWithTag("activityNameTextField")
                .performTextInput("Visit the Eiffel Tower")
            composeTestRule.onNodeWithTag("activityAddressTextField").performTextInput("Paris")

            composeTestRule.onNodeWithText("ADD").performClick()
        }

        composeTestRule.onAllNodesWithTag("activitiesAddButton")[0].performClick()

        composeTestRule.onNodeWithText("Error").assertIsDisplayed()
    }
}