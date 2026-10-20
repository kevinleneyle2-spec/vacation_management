package com.vacation.tripinmind.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vacation.tripinmind.R
import com.vacation.tripinmind.home.intent.VacationIntent
import com.vacation.tripinmind.util.UiEvent
import com.vacation.tripinmind.home.viewmodel.HomeViewModel
import com.vacation.tripinmind.model.Vacation
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onVacationDetailsClick: (vacationID: String?, isSHared: Boolean) -> Unit = { _, _ -> },
    onAddVacationClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val vacationUiState by viewModel.vacationState.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var vacationToDelete by remember { mutableStateOf<Vacation?>(null) }
    var archiveMessageResId by remember { mutableStateOf<Int?>(null) }
    var showQrcodeSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.homeUiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(event.message.asString(context))
                }
            }
        }
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            viewModel.handleIntent(VacationIntent.CreateShareCode)
        }
    }

    LaunchedEffect(archiveMessageResId) {
        if (archiveMessageResId != null) {
            delay(2000)
            archiveMessageResId = null
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (showQrcodeSheet) {
        QrcodeBottomSheet(
            onDismiss = { showQrcodeSheet = false },
            qrcode = vacationUiState.shareCode,
            sheetState = sheetState
        )
    }

    if (vacationToDelete != null) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    vacationToDelete?.let {
                        viewModel.handleIntent(VacationIntent.DeleteVacation(it))
                    }
                    vacationToDelete = null
                }) {
                    Text(
                        text = stringResource(id = R.string.common_delete_button),
                        color = colorResource(id = R.color.red)
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { vacationToDelete = null }) {
                    Text(
                        text = stringResource(id = R.string.common_cancel_button),
                        color = colorResource(id = R.color.black)
                    )
                }
            },
            title = {
                Text(
                    text = stringResource(id = R.string.homescreen_delete_vacation_title),
                    color = colorResource(id = R.color.orange)
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.homescreen_delete_vacation_message)
                )
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            HomeScreenContent(
                vacationUiState = vacationUiState,
                onDeleteVacation = { vacation ->
                    vacationToDelete = vacation
                },
                onArchiveVacation = { vacation ->
                    viewModel.handleIntent(VacationIntent.ArchiveVacation(vacation))
                    archiveMessageResId =
                        if (vacation.isArchived) R.string.homescreen_unarchive_message else R.string.homescreen_archive_message
                },
                onToggleShowFilterVacations = { vacationFilter ->
                    viewModel.handleIntent(VacationIntent.ToggleShowVacationFilter(vacationFilter))
                },
                onVacationDetailsClick = onVacationDetailsClick,
                onAddVacationClick = onAddVacationClick,
                onShowQrcode = { showQrcodeSheet = true },
                modifier = Modifier.fillMaxSize()
            )

            archiveMessageResId?.let { resId ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(colorResource(R.color.orange))
                            .padding(horizontal = 32.dp, vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = resId),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
