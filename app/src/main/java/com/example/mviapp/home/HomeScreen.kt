package com.example.mviapp.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.model.VacationDto
import com.example.mviapp.R
import com.example.mviapp.home.intent.VacationIntent
import com.example.mviapp.home.intent.VacationUiViewState
import com.example.mviapp.home.viewmodel.HomeViewModel
import com.example.mviapp.navigation.AppDestinations
import com.example.mviapp.ui.theme.MVIAppTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val vacationUiState by viewModel.vacationState.collectAsStateWithLifecycle()
    var vacationToDelete by remember { mutableStateOf<VacationDto?>(null) }

    if (vacationToDelete != null) {
        AlertDialog(
            onDismissRequest = { vacationToDelete = null },
            confirmButton = {
                TextButton(onClick = {
                    vacationToDelete?.let {
                        viewModel.handleIntent(VacationIntent.DeleteVacation(it))
                    }
                    vacationToDelete = null
                }) {
                    Text(
                        text = stringResource(id = R.string.confirm_button),
                        color = colorResource(id = R.color.red)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { vacationToDelete = null }) {
                    Text(
                        text = stringResource(id = R.string.cancel_button),
                        color = colorResource(id = R.color.black)
                    )
                }
            },
            title = { Text(
                text = stringResource(id = R.string.delete_vacation_title),
                color = colorResource(id = R.color.orange)
            ) },
            text = { Text(
                text = stringResource(id = R.string.delete_vacation_message)
            ) }
        )
    }

    HomeScreenContent(
        vacationUiState = vacationUiState,
        onDeleteVacation = { dto ->
            vacationToDelete = dto
        },
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@Composable
fun HomeScreenContent(
    vacationUiState: VacationUiViewState,
    onDeleteVacation: (VacationDto) -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background_home),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            if (vacationUiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "loader-rotation")
                    val angle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing)
                        ),
                        label = "loader-angle"
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Loading",
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(angle)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (vacationUiState.vacations.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = paddingValues,
                        ) {
                            items(
                                items = vacationUiState.vacations,
                                key = { vacation -> vacation.id }
                            ) { vacation ->
                                VacationItem(
                                    onDeleteClick = { onDeleteVacation(vacation) },
                                    vacationDto = vacation,
                                    onItemSelected = {
                                        onNavigate(AppDestinations.buildDetailsRoute(vacation.id))
                                    }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            TextButton(
                                onClick = {
                                    onNavigate(AppDestinations.INIT_ROUTE)
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = stringResource(R.string.homescreen_next_button),
                                    fontSize = 24.sp,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            TextButton(
                                onClick = {
                                    onNavigate(AppDestinations.INIT_ROUTE)
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    text = stringResource(R.string.homescreen_first_button),
                                    fontSize = 24.sp,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MVIAppTheme {
        HomeScreenContent(
            vacationUiState = VacationUiViewState(
                vacations = listOf(
                    VacationDto(id = 1, name = "Paris", nbrDay = 3, days = emptyList())
                )
            ),
            onDeleteVacation = {},
            onNavigate = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenEmptyPreview() {
    MVIAppTheme {
        HomeScreenContent(
            vacationUiState = VacationUiViewState(
                vacations = listOf()
            ),
            onDeleteVacation = {},
            onNavigate = {}
        )
    }
}
