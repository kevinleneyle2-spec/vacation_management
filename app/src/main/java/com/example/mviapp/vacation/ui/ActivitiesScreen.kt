package com.example.mviapp.vacation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.data.local.model.Day
import com.example.mviapp.ui.theme.MVIAppTheme
import com.example.mviapp.vacation.intent.InitIntent
import com.example.mviapp.vacation.intent.VacationState
import com.example.mviapp.vacation.viewmodel.InitViewModel

@Composable
fun ActivitiesScreen(
    viewModel: InitViewModel,
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initState by viewModel.initState.collectAsState()
    ActivitiesContent(
        state = initState,
        onBackClick = onBackClick,
        onUpdateDayName = { index, name ->
            viewModel.handleIntent(InitIntent.UpdateDayName(index, name))
        },
        onUpdateDayActivities = { index, activities ->
            viewModel.handleIntent(InitIntent.AddDayActivities(index, activities))
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
    onUpdateDayActivities: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "What have you planned?") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Activities of vacation")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                state.days.forEachIndexed { index, day ->
                    ActivitiesItem(
                        day = day,
                        onNameChange = { newValue ->
                            if (newValue.length <= 100 && !newValue.contains("\n")) {
                                onUpdateDayName(index, newValue)
                            }
                        },
                        onActivitiesChange = { newValue ->
                            onUpdateDayActivities(index, newValue)
                        },
                        onItemSelected = {})
                    Spacer(modifier = Modifier.height(16.dp))
                }
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
                        Day("Day 1", listOf("Visit the Eiffel Tower")),
                        Day("Day 2", listOf("Go to the Louvre"))
                    )
                ),
                onBackClick = {},
                onUpdateDayName = { _, _ -> },
                onUpdateDayActivities = { _, _ -> }
            )
        }
    }
}