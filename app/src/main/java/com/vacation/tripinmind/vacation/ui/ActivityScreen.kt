package com.vacation.tripinmind.vacation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vacation.tripinmind.R
import com.vacation.tripinmind.util.UiEvent
import com.vacation.tripinmind.vacation.intent.InitIntent
import com.vacation.tripinmind.vacation.viewmodel.InitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    viewModel: InitViewModel = hiltViewModel(),
    vacationId: String? = null,
    onHomeClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val initState by viewModel.initState.collectAsStateWithLifecycle()
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

    LaunchedEffect(vacationId) {
        if (vacationId != null) {
            viewModel.handleIntent(InitIntent.LoadVacation(vacationId))
        }
    }

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                            if (vacationId != null) {
                                viewModel.handleIntent(InitIntent.UpdateVacation(initState.toVacation()))
                                onBackClick()
                            } else {
                                viewModel.handleIntent(InitIntent.CreateVacation(initState.toVacation()))
                                onHomeClick()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("activityFinishButton"),
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
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets
    ) { paddingValues ->

        ActivitiesScreenContent(
            state = initState,
            onUpdateAddInfo = { index, name ->
                viewModel.handleIntent(InitIntent.UpdateAddInfo(index, name))
            },
            onAddDayActivities = { index, activity ->
                viewModel.handleIntent(InitIntent.AddDayActivities(index, activity))
            },
            onModifyDayActivities = { dayNumber, index, activity ->
                viewModel.handleIntent(InitIntent.UpdateDayActivities(dayNumber, index, activity))
            },
            onRemoveDayActivities = { dayNumber, index ->
                viewModel.handleIntent(InitIntent.RemoveDayActivities(dayNumber, index))
            },
            onAddIdea = { idea ->
                viewModel.handleIntent(InitIntent.AddIdea(idea))
            },
            onRemoveIdea = { index ->
                viewModel.handleIntent(InitIntent.RemoveIdea(index))
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}
