package com.vacation.tripinmind.vacation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vacation.tripinmind.R
import com.vacation.tripinmind.util.UiEvent
import com.vacation.tripinmind.vacation.intent.InitIntent
import com.vacation.tripinmind.vacation.model.VacationState
import com.vacation.tripinmind.vacation.viewmodel.InitViewModel
import kotlinx.coroutines.flow.StateFlow

interface InitViewModelActions {
    val initState: StateFlow<VacationState>
    val initValidation: StateFlow<Boolean>
    fun handleIntent(intent: InitIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitScreen(
    onActivitiesClick: () -> Unit = {},
    viewModel: InitViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val initState by viewModel.initState.collectAsStateWithLifecycle()
    val initValidation by viewModel.initValidation.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.initUiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val isEndDateEnabled = initState.startDate > 0L

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
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        InitScreenContent(
            initState = initState,
            initValidation = initValidation,
            onUpdateName = { name -> viewModel.handleIntent(InitIntent.UpdateName(name)) },
            onSelectStartDateClick = { showStartDatePicker = true },
            onSelectEndDateClick = { if (isEndDateEnabled) showEndDatePicker = true },
            onUpdateImage = { image -> viewModel.handleIntent(InitIntent.UpdateImage(image)) },
            onActivitiesClick = onActivitiesClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}