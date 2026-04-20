package com.example.mviapp.vacation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.model.Activity
import com.example.data.local.model.Day
import com.example.mviapp.R
import com.example.mviapp.ui.theme.MVIAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesItem(
    day: Day,
    onAddInfo: (String) -> Unit,
    onAddActivity: (String, String, String) -> Unit,
    onRemoveActivity: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var showTimeSheet by remember { mutableStateOf(false) }
    var showDurationSheet by remember { mutableStateOf(false) }
    var showAddActivityDialog by remember { mutableStateOf(false) }
    val timeListState = rememberLazyListState()
    val durationListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var newActivityValue by remember { mutableStateOf("") }
    var activityTimeValue by remember { mutableStateOf("12h00") }
    var activityDurationValue by remember { mutableStateOf("01h00") }

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

    fun checkAddAllowed(): Boolean {
        return (newActivityValue.isNotBlank()
                && activityTimeValue.isNotBlank()
                && activityDurationValue.isNotBlank())
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .testTag("vacationCard"),
        border = BorderStroke(
            width = 3.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    MaterialTheme.colorScheme.primary
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.white_orange)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = day.nameDay,
                    color = colorResource(R.color.white),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Column(
            modifier = modifier.padding(all = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.news_ico),
                        contentDescription = "news ico",
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = stringResource(R.string.activitiesscreen_additional_info_title),
                        color = colorResource(R.color.orange),
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedTextField(
                    value = day.additionalInfo,
                    onValueChange = { newValue ->
                        if (newValue.length <= 100 && !newValue.contains("\n")) {
                            onAddInfo(newValue)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    label = { Text(stringResource(R.string.activitiesscreen_additional_info_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.activities_ico),
                        contentDescription = "activities ico",
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = stringResource(R.string.activitiesscreen_activities_title),
                        color = colorResource(R.color.orange),
                        fontWeight = FontWeight.Bold
                    )
                }

                if (day.activity.isEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.activitiesscreen_activities_empty),
                            modifier = Modifier
                                .weight(1f)
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
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            IconButton(
                onClick = { showAddActivityDialog = true },
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.orange))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add activity",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            if (showAddActivityDialog) {
                Dialog(onDismissRequest = { showAddActivityDialog = false }) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stringResource(R.string.activitiesscreen_activities_add_title),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.orange)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = newActivityValue,
                                onValueChange = { newActivityValue = it },
                                label = { Text(stringResource(R.string.activitiesscreen_activities_description)) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { showTimeSheet = true }) {
                                    OutlinedTextField(
                                        value = activityTimeValue,
                                        onValueChange = {},
                                        readOnly = true,
                                        enabled = false,
                                        label = { Text(stringResource(R.string.activitiesscreen_time_title)) },
                                        trailingIcon = {
                                            Icon(
                                                Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                            disabledBorderColor = MaterialTheme.colorScheme.primary,
                                            disabledLabelColor = MaterialTheme.colorScheme.primary,
                                            disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
                                            disabledContainerColor = Color.Transparent
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { showDurationSheet = true }) {
                                    OutlinedTextField(
                                        value = activityDurationValue,
                                        onValueChange = {},
                                        readOnly = true,
                                        enabled = false,
                                        label = { Text(stringResource(R.string.activitiesscreen_duration_title)) },
                                        trailingIcon = {
                                            Icon(
                                                Icons.Default.AccessTime,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                            disabledBorderColor = MaterialTheme.colorScheme.primary,
                                            disabledLabelColor = MaterialTheme.colorScheme.primary,
                                            disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
                                            disabledContainerColor = Color.Transparent
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { showAddActivityDialog = false }) {
                                    Text(stringResource(R.string.cancel_button))
                                }
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
                                            activityDurationValue = "01h00"
                                            showAddActivityDialog = false
                                        }
                                    },
                                    enabled = checkAddAllowed()
                                ) {
                                    Text(stringResource(R.string.activitiesscreen_add_button))
                                }
                            }
                        }
                    }
                }
            }

            if (showTimeSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showTimeSheet = false },
                    containerColor = Color.White
                ) {
                    LaunchedEffect(Unit) {
                        val index = timeOptions.indexOf(activityTimeValue)
                        if (index != -1) {
                            timeListState.scrollToItem(index)
                        }
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
                                        textAlign = TextAlign.Center,
                                        color = if (time == activityTimeValue) colorResource(R.color.orange) else Color.Black
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

            if (showDurationSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showDurationSheet = false },
                    containerColor = Color.White
                ) {
                    LaunchedEffect(Unit) {
                        val index = durationOptions.indexOf(activityDurationValue)
                        if (index != -1) {
                            durationListState.scrollToItem(index)
                        }
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
                                        textAlign = TextAlign.Center,
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
    }
}

@Preview(showBackground = true)
@Composable
fun ActivitiesItemPreview() {
    MVIAppTheme {
        val mockDay = Day(
            nameDay = "Lundi 5 mai 2025",
            additionalInfo = "Additional info",
            activity = listOf(
                Activity("Visit Eiffel Tower", "12h00", "2h00"),
                Activity("Louvre Museum", "12h00", "2h00")
            )
        )

        ActivitiesItem(
            day = mockDay,
            onAddInfo = {},
            onAddActivity = { _, _, _ -> },
            onRemoveActivity = {}
        )
    }
}
