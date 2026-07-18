package com.vacation.tripinmind.vacation.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vacation.tripinmind.R
import com.vacation.tripinmind.ui.theme.MVIAppTheme
import com.vacation.tripinmind.vacation.intent.InitIntent
import com.vacation.tripinmind.vacation.model.VacationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

interface InitViewModelActions {
    val initState: StateFlow<VacationState>
    val initValidation: StateFlow<Boolean>
    fun handleIntent(intent: InitIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitScreen(
    viewModel: InitViewModelActions,
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val localContext = LocalContext.current
    val initState by viewModel.initState.collectAsStateWithLifecycle()
    val initValidation by viewModel.initValidation.collectAsStateWithLifecycle()

    LaunchedEffect(initState.errorMessage) {
        initState.errorMessage?.let { uiText ->
            Toast.makeText(localContext, uiText.asString(localContext), Toast.LENGTH_LONG).show()
        }
    }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val isEndDateEnabled = initState.startDate > 0L

    val icons = listOf(
        "vacation_ico" to R.drawable.vacation_ico,
        "beach_ico" to R.drawable.beach_ico,
        "ski_ico" to R.drawable.ski_ico,
        "forest_ico" to R.drawable.forest_ico,
        "plane_ico" to R.drawable.plane_ico
    )

    if (showStartDatePicker) {
        val startDateRangePickerState = key(showStartDatePicker) {
            rememberDateRangePickerState(
                initialSelectedStartDateMillis = if (initState.startDate > 0L) initState.startDate else null,
                initialSelectedEndDateMillis = if (initState.endDate > 0L) initState.endDate else null
            )
        }

        LaunchedEffect(
            startDateRangePickerState.selectedStartDateMillis,
            startDateRangePickerState.selectedEndDateMillis
        ) {
            val curStart = startDateRangePickerState.selectedStartDateMillis
            val curEnd = startDateRangePickerState.selectedEndDateMillis

            if (initState.endDate > 0L && curStart != null && curEnd == null && curStart != initState.startDate) {
                if (curStart <= initState.endDate) {
                    startDateRangePickerState.setSelection(curStart, initState.endDate)
                } else {
                    startDateRangePickerState.setSelection(curStart, curStart)
                }
            }
        }

        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = startDateRangePickerState.selectedStartDateMillis
                        val end = startDateRangePickerState.selectedEndDateMillis
                        if (start != null) {
                            viewModel.handleIntent(InitIntent.UpdateStartDate(start))
                        }
                        if (end != null) {
                            viewModel.handleIntent(InitIntent.UpdateEndDate(end))
                        }
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text(stringResource(R.string.common_cancel_button))
                }
            }
        ) {
            DateRangePicker(
                state = startDateRangePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = colorResource(R.color.orange),
                    selectedDayContentColor = Color.White,
                    dayInSelectionRangeContainerColor = colorResource(R.color.orange).copy(alpha = 0.2f),
                    dayInSelectionRangeContentColor = colorResource(R.color.orange),
                    selectedYearContainerColor = colorResource(R.color.orange),
                    selectedYearContentColor = Color.White,
                    todayContentColor = colorResource(R.color.orange),
                    todayDateBorderColor = colorResource(R.color.orange)
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (showEndDatePicker && isEndDateEnabled) {
        val endDateRangePickerState = key(showEndDatePicker) {
            rememberDateRangePickerState(
                initialSelectedStartDateMillis = initState.startDate,
                initialSelectedEndDateMillis = if (initState.endDate > 0L) initState.endDate else initState.startDate,
                initialDisplayedMonthMillis = initState.startDate,
                selectableDates = object : SelectableDates {
                    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                        return utcTimeMillis >= initState.startDate
                    }
                }
            )
        }

        LaunchedEffect(
            endDateRangePickerState.selectedStartDateMillis,
            endDateRangePickerState.selectedEndDateMillis
        ) {
            val curStart = endDateRangePickerState.selectedStartDateMillis
            val curEnd = endDateRangePickerState.selectedEndDateMillis

            if (curStart != null && curEnd == null && curStart != initState.startDate) {
                if (curStart >= initState.startDate) {
                    endDateRangePickerState.setSelection(initState.startDate, curStart)
                } else {
                    endDateRangePickerState.setSelection(initState.startDate, initState.startDate)
                }
            }
        }

        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val end = endDateRangePickerState.selectedEndDateMillis
                            ?: endDateRangePickerState.selectedStartDateMillis
                            ?: initState.startDate
                        viewModel.handleIntent(InitIntent.UpdateEndDate(end))
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text(stringResource(R.string.common_cancel_button))
                }
            }
        ) {
            DateRangePicker(
                state = endDateRangePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = colorResource(R.color.orange),
                    selectedDayContentColor = Color.White,
                    dayInSelectionRangeContainerColor = colorResource(R.color.orange).copy(alpha = 0.2f),
                    dayInSelectionRangeContentColor = colorResource(R.color.orange),
                    selectedYearContainerColor = colorResource(R.color.orange),
                    selectedYearContentColor = Color.White,
                    todayContentColor = colorResource(R.color.orange),
                    todayDateBorderColor = colorResource(R.color.orange)
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.initscreen_title),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.initscreen_name_title),
                color = colorResource(R.color.orange),
                modifier = Modifier.align(Alignment.Start)
            )

            OutlinedTextField(
                value = initState.vacationName,
                onValueChange = { newValue ->
                    val truncated = if (newValue.length > 25) newValue.take(25) else newValue
                    if (!truncated.contains("\n")) {
                        viewModel.handleIntent(InitIntent.UpdateName(truncated))
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = colorResource(R.color.orange),
                    focusedBorderColor = colorResource(R.color.orange),
                    unfocusedBorderColor = colorResource(R.color.orange),
                    focusedLabelColor = colorResource(R.color.orange),
                    unfocusedLabelColor = colorResource(R.color.orange)
                ),
                trailingIcon = {
                    if (initState.vacationName.isNotEmpty()) {
                        IconButton(onClick = { viewModel.handleIntent(InitIntent.UpdateName("")) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear vacation name",
                                tint = colorResource(R.color.orange)
                            )
                        }
                    }
                },
                label = {
                    Text(
                        stringResource(R.string.initscreen_name_description),
                        color = colorResource(R.color.orange)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("initNameTextField"),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.initscreen_date_title),
                color = colorResource(R.color.orange),
                modifier = Modifier.align(Alignment.Start)
            )
            val formattedStartDate = if (initState.startDate > 0L) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                sdf.format(Date(initState.startDate))
            } else ""

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showStartDatePicker = true }) {
                OutlinedTextField(
                    value = formattedStartDate,
                    onValueChange = { },
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = colorResource(R.color.orange),
                        disabledLabelColor = colorResource(R.color.orange),
                        disabledTrailingIconColor = colorResource(R.color.orange),
                        disabledPlaceholderColor = colorResource(R.color.orange),
                        disabledContainerColor = Color.Transparent
                    ),
                    label = {
                        Text(
                            stringResource(R.string.initscreen_date_description),
                            color = colorResource(R.color.orange)
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showStartDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select Start Date",
                                tint = colorResource(R.color.orange)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStartDatePicker = true }
                        .testTag("initDateTextField"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            val orangeColor = colorResource(R.color.orange)
            val endFieldColor = if (isEndDateEnabled) orangeColor else orangeColor.copy(alpha = 0.4f)

            Text(
                text = stringResource(R.string.initscreen_end_date_title),
                color = endFieldColor,
                modifier = Modifier.align(Alignment.Start)
            )
            val formattedEndDate = if (initState.endDate > 0L) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                sdf.format(Date(initState.endDate))
            } else ""

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isEndDateEnabled) Modifier.clickable { showEndDatePicker = true }
                        else Modifier
                    )
            ) {
                OutlinedTextField(
                    value = formattedEndDate,
                    onValueChange = { },
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = if (isEndDateEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        disabledBorderColor = endFieldColor,
                        disabledLabelColor = endFieldColor,
                        disabledTrailingIconColor = endFieldColor,
                        disabledPlaceholderColor = endFieldColor,
                        disabledContainerColor = Color.Transparent
                    ),
                    label = {
                        Text(
                            stringResource(R.string.initscreen_end_date_description),
                            color = endFieldColor
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { if (isEndDateEnabled) showEndDatePicker = true },
                            enabled = isEndDateEnabled
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select End Date",
                                tint = endFieldColor
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (isEndDateEnabled) Modifier.clickable { showEndDatePicker = true }
                            else Modifier
                        )
                        .testTag("initEndDateTextField"),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.initscreen_choose_icon_text),
                color = colorResource(R.color.orange),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                icons.forEach { (name, resId) ->
                    val isSelected = initState.image == name
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) colorResource(R.color.orange).copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .border(
                                width = 2.dp,
                                color = if (isSelected) colorResource(R.color.orange)
                                else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                viewModel.handleIntent(InitIntent.UpdateImage(name))
                            }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(
                onClick = {
                    onNavigate("activities")
                },
                enabled = initValidation,
                modifier = Modifier
                    .align(Alignment.End)
                    .testTag("initNextButton")
            ) {
                Text(
                    text = stringResource(R.string.initscreen_next_button),
                    fontSize = 24.sp,
                    color = if (initValidation)
                        colorResource(R.color.orange)
                    else
                        Color.Gray.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InitScreenPreview() {
    val mockActions = object : InitViewModelActions {
        override val initState = MutableStateFlow(
            VacationState(vacationName = "Summer Trip", startDate = 1683705600000L, endDate = 1684051200000L)
        )
        override val initValidation = MutableStateFlow(true)
        override fun handleIntent(intent: InitIntent) {}
    }

    MVIAppTheme {
        InitScreen(viewModel = mockActions)
    }
}

@Preview(showBackground = true)
@Composable
fun InitScreenEmptyPreview() {
    val mockActions = object : InitViewModelActions {
        override val initState = MutableStateFlow(VacationState())
        override val initValidation = MutableStateFlow(false)
        override fun handleIntent(intent: InitIntent) {}
    }

    MVIAppTheme {
        InitScreen(viewModel = mockActions)
    }
}