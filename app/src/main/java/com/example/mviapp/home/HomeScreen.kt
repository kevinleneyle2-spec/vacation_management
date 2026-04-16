package com.example.mviapp.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
            title = {
                Text(
                    text = stringResource(id = R.string.delete_vacation_title),
                    color = colorResource(id = R.color.orange)
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.delete_vacation_message)
                )
            }
        )
    }

    HomeScreenContent(
        vacationUiState = vacationUiState,
        onDeleteVacation = { dto ->
            vacationToDelete = dto
        },
        onArchiveVacation = { dto ->
            viewModel.handleIntent(VacationIntent.ArchiveVacation(dto))
        },
        onToggleShowArchived = {
            viewModel.handleIntent(VacationIntent.ToggleShowArchived)
        },
        onNavigate = onNavigate,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    vacationUiState: VacationUiViewState,
    onDeleteVacation: (VacationDto) -> Unit,
    onArchiveVacation: (VacationDto) -> Unit,
    onToggleShowArchived: () -> Unit,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_home),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                if (vacationUiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        val infiniteTransition =
                            rememberInfiniteTransition(label = "loader-rotation")
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
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                .height(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .border(
                                    1.dp,
                                    Color.White.copy(alpha = 0.3f),
                                    RoundedCornerShape(24.dp)
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                        if (!vacationUiState.showArchived) colorResource(R.color.orange) else colorResource(
                                            R.color.orange
                                        ).copy(alpha = 0.4f)
                                    )
                                    .clickable { if (vacationUiState.showArchived) onToggleShowArchived() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.homescreen_projects_button),
                                    color = Color.White,
                                    fontWeight = if (!vacationUiState.showArchived) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            }

                            Spacer(Modifier.width(16.dp))

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                        if (vacationUiState.showArchived) colorResource(R.color.orange) else colorResource(
                                            R.color.orange
                                        ).copy(alpha = 0.4f)
                                    )
                                    .clickable { if (!vacationUiState.showArchived) onToggleShowArchived() },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Archive,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                        tint = Color.White
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = stringResource(R.string.homescreen_archives_button),
                                        color = Color.White,
                                        fontWeight = if (vacationUiState.showArchived) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                        }

                        val filteredVacations = vacationUiState.vacations.filter {
                            it.isArchived == vacationUiState.showArchived
                        }

                        if (filteredVacations.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(top = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                items(
                                    items = filteredVacations,
                                    key = { vacation -> vacation.id }
                                ) { vacation ->
                                    VacationItem(
                                        onDeleteClick = { onDeleteVacation(vacation) },
                                        vacationDto = vacation,
                                        onItemSelected = {
                                            onNavigate(AppDestinations.buildDetailsRoute(vacation.id))
                                        },
                                        onArchiveClick = { onArchiveVacation(vacation) }
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (vacationUiState.showArchived) {
                                        stringResource(id = R.string.homescreen_no_archived_vacation)
                                    } else {
                                        stringResource(id = R.string.homescreen_no_vacation)
                                    },
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center
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
                    VacationDto(
                        id = 1,
                        name = "Paris",
                        nbrDay = 3,
                        days = emptyList(),
                        ideas = emptyList(),
                        image = "vacation_ico",
                        isArchived = false
                    )
                )
            ),
            onDeleteVacation = {},
            onArchiveVacation = {},
            onToggleShowArchived = {},
            onNavigate = {}
        )
    }
}
