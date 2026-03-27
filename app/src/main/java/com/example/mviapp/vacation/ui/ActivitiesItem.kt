package com.example.mviapp.vacation.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.local.model.Activity
import com.example.data.local.model.Day
import com.example.mviapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesItem(
    day: Day,
    onNameChange: (String) -> Unit,
    onAddActivity: (String, String, String) -> Unit,
    onRemoveActivity: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    var showTimeSheet by remember { mutableStateOf(false) }
    var showDurationSheet by remember { mutableStateOf(false) }
    val timeListState = rememberLazyListState()
    val durationListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var newActivityValue by remember { mutableStateOf("") }
    var activityTimeValue by remember { mutableStateOf("12h00") }
    var activityDurationValue by remember { mutableStateOf("") }

    val timeOptions = remember {
        (0..23).flatMap { hour ->
            listOf("00", "15", "30", "45").map { min ->
                "${hour.toString().padStart(2, '0')}h$min"
            }
        }
    }

    val durationOptions = remember {
        (0..6).flatMap { hour ->
            listOf("00", "15", "30", "45").map { min ->
                "${hour.toString().padStart(2, '0')}h$min"
            }
        }
    }

    fun checkAddAllowed() : Boolean {
        return (newActivityValue.isNotBlank()
            && activityTimeValue.isNotBlank()
            && activityDurationValue.isNotBlank())
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .testTag("vacationCard"),
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
                color = colorResource(R.color.orange),
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = day.nameDay,
                onValueChange = { newValue ->
                    if (newValue.length <= 100 && !newValue.contains("\n")) {
                        onNameChange(newValue)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.primary
                ),
                label = { Text(stringResource(R.string.activitiesscreen_name_day_description)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.activitiesscreen_activities_title),
                color = colorResource(R.color.orange),
                fontWeight = FontWeight.Bold
            )

            if(day.activity.isEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.activitiesscreen_activities_empty),
                        modifier = Modifier.weight(1f)
                            .padding(bottom = 8.dp)
                    )
                }
            }

            day.activity.forEachIndexed { index, activity ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = buildString {
                            append("- ")
                            append(activity.activityTime)
                            append(" ")
                            append(activity.activityName)
                            append(" (")
                            append(activity.activityDuration)
                            append(")")
                        },
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = { onRemoveActivity(index) },
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

            Column(
                Modifier
                    .border(
                        width = 2.dp,
                        color = colorResource(R.color.orange),
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(all = 8.dp)
            ) {
                Text(
                    text = "New Activity",
                    color = colorResource(R.color.orange)
                )

                Row(modifier = Modifier.fillMaxWidth()) {

                    OutlinedTextField(
                        value = newActivityValue,
                        onValueChange = { newValue ->
                            newActivityValue = newValue
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.primary
                        ),
                        label = { Text(stringResource(R.string.activitiesscreen_activities_description)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                            }
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(
                        8.dp
                    )
                ) {

                    OutlinedTextField(
                        value = activityTimeValue,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                stringResource(R.string.activitiesscreen_time_title),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { if (it.isFocused) showTimeSheet = true },
                        trailingIcon = {
                            IconButton(onClick = { showTimeSheet = true }) {
                                Icon(Icons.Default.AccessTime, contentDescription = null)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        interactionSource = remember { MutableInteractionSource() }
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect {
                                        if (it is PressInteraction.Release) showTimeSheet = true
                                    }
                                }
                            }
                    )
                    if (showTimeSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showTimeSheet = false },
                            containerColor = Color.White
                        ) {
                            LaunchedEffect(Unit) {
                                val selectedIndex =
                                    timeOptions.indexOf(activityTimeValue).coerceAtLeast(0)
                                timeListState.scrollToItem(selectedIndex)
                            }

                            LazyColumn(
                                state = timeListState,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                            ) {
                                items(timeOptions) { time ->
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = time,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                color = if (time == activityTimeValue) colorResource(
                                                    R.color.orange
                                                ) else Color.Black
                                            )
                                        },
                                        modifier = Modifier.clickable {
                                            activityTimeValue = time
                                            showTimeSheet = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = activityDurationValue,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                text = stringResource(R.string.activitiesscreen_duration_title),
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged { if (it.isFocused) showDurationSheet = true },
                        trailingIcon = {
                            IconButton(onClick = { showDurationSheet = true }) {
                                Icon(Icons.Default.AccessTime, contentDescription = null)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        interactionSource = remember { MutableInteractionSource() }
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect {
                                        if (it is PressInteraction.Release) showDurationSheet = true
                                    }
                                }
                            }
                    )
                    if (showDurationSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showDurationSheet = false },
                            containerColor = Color.White
                        ) {
                            LaunchedEffect(Unit) {
                                val selectedIndex =
                                    durationOptions.indexOf(activityDurationValue).coerceAtLeast(0)
                                durationListState.scrollToItem(selectedIndex)
                            }

                            LazyColumn(
                                state = durationListState,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                            ) {
                                items(durationOptions) { duration ->
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = duration,
                                                modifier = Modifier.fillMaxWidth(),
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                color = if (duration == activityDurationValue) colorResource(
                                                    R.color.orange
                                                ) else Color.Black
                                            )
                                        },
                                        modifier = Modifier.clickable {
                                            activityDurationValue = duration
                                            showDurationSheet = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            if (checkAddAllowed()) {
                                onAddActivity(
                                    newActivityValue,
                                    activityTimeValue,
                                    activityDurationValue
                                )

                                newActivityValue = ""
                                activityTimeValue = "12h00"
                                activityDurationValue = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = if (checkAddAllowed()) {
                                colorResource(id = R.color.orange)
                            } else { Color.Gray.copy(alpha = 0.2f) },
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.activitiesscreen_add_button),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivitiesItemPreview() {
    val mockDay = Day(
        nameDay = "Day 1: Paris Visit",
        activity = listOf(
            Activity("Visit Eiffel Tower", "12h00", "2h00"),
            Activity("Louvre Museum", "12h00", "2h00")
        )
    )

    ActivitiesItem(
        day = mockDay,
        onNameChange = {},
        onAddActivity = { _, _, _ -> },
        onRemoveActivity = {}
    )
}