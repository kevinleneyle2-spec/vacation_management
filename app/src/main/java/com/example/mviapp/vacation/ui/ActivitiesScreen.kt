package com.example.mviapp.vacation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.model.Activity
import com.example.data.local.model.Day
import com.example.mviapp.R
import com.example.mviapp.ui.theme.MVIAppTheme
import com.example.mviapp.vacation.intent.InitIntent
import com.example.mviapp.vacation.intent.VacationState
import com.example.mviapp.vacation.viewmodel.InitViewModel

@Composable
fun ActivitiesScreen(
    viewModel: InitViewModel,
    vacationId: Int? = null,
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initState by viewModel.initState.collectAsState()

    LaunchedEffect(vacationId) {
        if (vacationId != null) {
            viewModel.handleIntent(InitIntent.LoadVacation(vacationId))
        }
    }

    ActivitiesContent(
        state = initState,
        onBackClick = onBackClick,
        onUpdateDayName = { index, name ->
            viewModel.handleIntent(InitIntent.UpdateDayName(index, name))
        },
        onAddDayActivities = { index, activity ->
            viewModel.handleIntent(InitIntent.AddDayActivities(index, activity))
        },
        onRemoveDayActivities = { dayNumber, index ->
            viewModel.handleIntent(InitIntent.RemoveDayActivities(dayNumber, index))
        },
        onCreateVacation = {
            if (vacationId != null) {
                viewModel.handleIntent(InitIntent.UpdateVacation(initState.toVacationDto()))
            } else {
                viewModel.handleIntent(InitIntent.CreateVacation(initState.toVacationDto()))
            }

            onNavigate("home")
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesContent(
    state: VacationState,
    onBackClick: () -> Unit,
    onUpdateDayName: (Int, String) -> Unit,
    onAddDayActivities: (Int, Activity) -> Unit,
    onRemoveDayActivities: (Int, Int) -> Unit,
    onCreateVacation: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.activitiesscreen_title),
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
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            onCreateVacation()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.activitiesscreen_finish_button),
                            fontSize = 24.sp
                        )
                    }
                }
            }
        },
        contentWindowInsets = androidx.compose.material3.ScaffoldDefaults.contentWindowInsets,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.activitiesscreen_description),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            itemsIndexed(state.days) { index, day ->
                ActivitiesItem(
                    day = day,
                    onNameChange = { newValue ->
                        if (newValue.length <= 100 && !newValue.contains("\n")) {
                            onUpdateDayName(index, newValue)
                        }
                    },
                    onAddActivity = { name, time, duration ->
                        onAddDayActivities(index, Activity(name, time, duration))
                    },
                    onRemoveActivity = { removeIndex ->
                        onRemoveDayActivities(index, removeIndex)
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivitiesScreenPreview() {
    MVIAppTheme {
        Surface {
            ActivitiesContent(
                state = VacationState(
                    days = listOf(
                        Day("Day 1", listOf(Activity("Visit the Eiffel Tower", "10:00", "2h00"))),
                        Day("Day 2", listOf(Activity("Go to the Louvre", "14:00", "2h00")))
                    )
                ),
                onBackClick = {},
                onUpdateDayName = { _, _ -> },
                onAddDayActivities = { _, _ -> },
                onRemoveDayActivities = { _, _ -> },
                onCreateVacation = {}
            )
        }
    }
}
