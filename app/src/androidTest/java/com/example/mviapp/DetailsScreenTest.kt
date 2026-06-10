package com.example.mviapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.mviapp.home.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class DetailsScreenTest {

    @Rule
    @JvmField
    var composeTestRule: ComposeContentTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun details_screen_works() {
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

        composeTestRule.onNodeWithTag("activityFinishButton").performClick()

        composeTestRule.onNodeWithText("PREPARE YOUR NEXT VACATION")

        composeTestRule.onAllNodesWithTag("vacationCard")[0].performClick()

        composeTestRule.onNodeWithText("Paris").assertIsDisplayed()
        composeTestRule.onNodeWithText("No idea for the moment").assertIsDisplayed()

        composeTestRule.onNodeWithTag("detailsBackButton").performClick()

        composeTestRule.onNodeWithText("PREPARE YOUR NEXT VACATION")
    }
}