package com.vacation.tripinmind

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class InitScreenTest {
    @Rule
    @JvmField
    var composeTestRule: ComposeContentTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun init_completed_fields_work() {
        val date = LocalDate.now().plusDays(1)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)

        val formattedDate = date.format(formatter)
        val formattedEndDate = date.plusDays(1).format(formatter)

        composeTestRule.onNodeWithTag("newVacationButton").performClick()

        composeTestRule.onNodeWithText("Your vacation").assertIsDisplayed()

        composeTestRule.onNodeWithTag("initNameTextField").performTextInput("Paris")
        composeTestRule.onNodeWithTag("initStartDateTextField").performClick()
        composeTestRule.onNodeWithText(formattedDate).performClick()
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithTag("initEndDateTextField").performClick()
        composeTestRule.onNodeWithText(formattedEndDate).performClick()
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithTag("initNextButton").performClick()

        composeTestRule.onNodeWithText("What have you planned?").assertIsDisplayed()
    }

    @Test
    fun init_num_day_missing() {
        val date = LocalDate.now().plusDays(1)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)

        val formattedDate = date.format(formatter)

        composeTestRule.onNodeWithTag("newVacationButton").performClick()

        composeTestRule.onNodeWithText("Your vacation").assertIsDisplayed()

        composeTestRule.onNodeWithTag("initNameTextField").performTextInput("Paris")
        composeTestRule.onNodeWithTag("initStartDateTextField").performClick()
        composeTestRule.onNodeWithText(formattedDate).performClick()
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithTag("initNextButton").performClick()

        composeTestRule.onNodeWithText("What have you planned?").assertIsNotDisplayed()
    }

    @Test
    fun init_name_missing() {
        val date = LocalDate.now().plusDays(1)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)

        val formattedDate = date.format(formatter)
        val formattedEndDate = date.plusDays(1).format(formatter)

        composeTestRule.onNodeWithTag("newVacationButton").performClick()

        composeTestRule.onNodeWithText("Your vacation").assertIsDisplayed()

        composeTestRule.onNodeWithTag("initStartDateTextField").performClick()
        composeTestRule.onNodeWithText(formattedDate).performClick()
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithTag("initEndDateTextField").performClick()
        composeTestRule.onNodeWithText(formattedEndDate).performClick()
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithTag("initNextButton").performClick()

        composeTestRule.onNodeWithText("What have you planned?").assertIsNotDisplayed()
    }

    @Test
    fun init_date_missing() {
        val date = LocalDate.now().plusDays(1)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)

        val formattedEndDate = date.plusDays(1).format(formatter)

        composeTestRule.onNodeWithTag("newVacationButton").performClick()

        composeTestRule.onNodeWithText("Your vacation").assertIsDisplayed()

        composeTestRule.onNodeWithTag("initNameTextField").performTextInput("Paris")

        composeTestRule.onNodeWithTag("initNextButton").performClick()

        composeTestRule.onNodeWithText("What have you planned?").assertIsNotDisplayed()
    }

    @Test
    fun init_name_too_long() {
        composeTestRule.onNodeWithTag("newVacationButton").performClick()

        composeTestRule.onNodeWithText("Your vacation").assertIsDisplayed()


        val longName = "A".repeat(30)
        composeTestRule.onNodeWithTag("initNameTextField").performTextInput(longName)

        composeTestRule.onNodeWithTag("initNameTextField").assert(hasText("A".repeat(25)))
    }

    @Test
    fun init_num_day_wrong() {
        val date = LocalDate.now().plusDays(1)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)

        val formattedDate = date.format(formatter)
        val formattedEndDate = date.plusMonths(1).format(formatter)

        composeTestRule.onNodeWithTag("newVacationButton").performClick()

        composeTestRule.onNodeWithText("Your vacation").assertIsDisplayed()

        composeTestRule.onNodeWithTag("initNameTextField").performTextInput("Paris")
        composeTestRule.onNodeWithTag("initStartDateTextField").performClick()
        composeTestRule.onNodeWithText(formattedDate).performClick()
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithTag("initEndDateTextField").performClick()
        composeTestRule.onNodeWithText(formattedEndDate).performScrollTo().performClick()
        composeTestRule.onNodeWithText("OK").performClick()

        composeTestRule.onNodeWithTag("initNextButton").performClick()

        composeTestRule.onNodeWithText("What have you planned?").assertIsNotDisplayed()
    }
}