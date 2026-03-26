package com.example.mviapp.vacation.ui

import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.local.model.Activity
import com.example.data.local.model.Day
import com.example.mviapp.R


@Composable
fun ActivitiesItem(
    day: Day,
    onNameChange: (String) -> Unit,
    onActivitiesChange: (String) -> Unit,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    var newActivityValue by remember { mutableStateOf("") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .testTag("vacationCard"),
        onClick = { onItemSelected(day.nameDay) },
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = colorResource(R.color.orange)
        ),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = colorResource(R.color.white_orange)
        )
    ) {
        Column(
            modifier = modifier.padding(all = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.activitiesscreen_name_day_title),
                color = colorResource(R.color.orange)
            )
            OutlinedTextField(
                value = day.nameDay,
                onValueChange = { newValue ->
                    if (newValue.length <= 100 && !newValue.contains("\n")) {
                        onNameChange(newValue)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = colorResource(R.color.orange),
                    focusedBorderColor = colorResource(R.color.orange),
                    unfocusedBorderColor = colorResource(R.color.orange),
                    focusedLabelColor = colorResource(R.color.orange),
                    unfocusedLabelColor = colorResource(R.color.orange)
                ),
                label = { Text(stringResource(R.string.activitiesscreen_name_day_description)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.activitiesscreen_activities_title),
                color = colorResource(R.color.orange)
            )

            day.activity.forEach { activity ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "- " + activity.activityName + " (" + activity.activityTime + ")")
                    TextButton(
                        onClick = {  },
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(width = 32.dp, height = 32.dp)
                            .testTag("activitiesDeleteButton"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "X",
                            color = colorResource(R.color.red),
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }
            }

            Row() {
                OutlinedTextField(
                    value = newActivityValue,
                    onValueChange = { newValue ->
                        newActivityValue = newValue
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = colorResource(R.color.orange),
                        focusedBorderColor = colorResource(R.color.orange),
                        unfocusedBorderColor = colorResource(R.color.orange),
                        focusedLabelColor = colorResource(R.color.orange),
                        unfocusedLabelColor = colorResource(R.color.orange)
                    ),
                    label = { Text(stringResource(R.string.activitiesscreen_activities_description)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (newActivityValue.isNotBlank()) {
                                onActivitiesChange(newActivityValue)
                                newActivityValue = ""
                            }
                            keyboardController?.hide()
                        }
                    )
                )

                TextButton(
                    onClick = {
                        if (newActivityValue.isNotBlank()) {
                            onActivitiesChange(newActivityValue)
                            newActivityValue = ""
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = colorResource(id = R.color.orange),
                        contentColor = Color.White
                    ),
                    modifier = modifier
                        .padding(start = 16.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = stringResource(R.string.activitiesscreen_add_button),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivitiesItemPreview() {
    // Mock data for the preview
    val mockDay = Day(
        nameDay = "Day 1: Paris Visit",
        activity = listOf(
            Activity("Visit Eiffel Tower", "12h"),
            Activity("Louvre Museum", "12h")
        )
    )

    ActivitiesItem(
        day = mockDay,
        onNameChange = {},
        onActivitiesChange = {},
        onItemSelected = {}
    )
}