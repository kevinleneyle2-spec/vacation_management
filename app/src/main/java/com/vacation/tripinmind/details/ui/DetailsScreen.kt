package com.vacation.tripinmind.details.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.vacation.tripinmind.R
import com.vacation.tripinmind.details.intent.DetailsIntent
import com.vacation.tripinmind.details.model.NavigationAppEnum
import com.vacation.tripinmind.details.viewmodel.DetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onIntent: (DetailsIntent) -> Unit = {}

) {
    val vacation by viewModel.vacation.collectAsState()
    var selectedLocation by remember { mutableStateOf<String?>(null) }
    var showSharedVacationSheet by remember { mutableStateOf(false) }
    var showLocationSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val packageManager = context.packageManager

    val isGoogleMapsInstalled = remember {
        try {
            packageManager.getPackageInfo("com.google.android.apps.maps", 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    val isWazeInstalled = remember {
        try {
            packageManager.getPackageInfo("com.waze", 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (showSharedVacationSheet) {
        SharedVacationBottomSheet(
            onDismiss = { showSharedVacationSheet = false },
            onAddViewer = { newValue ->
                onIntent(
                    DetailsIntent.AddViewer(
                        vacation = vacation?.vacation,
                        shareCode = newValue
                    )
                )
            },
            onClearError = { onIntent(DetailsIntent.ClearError) },
            onRemoveSharedUser = { index ->
                onIntent(
                    DetailsIntent.RemoveViewer(
                        index = index
                    )
                )
            },
            vacationModel = vacation,
            error = vacation?.error,
            sheetState = sheetState
        )
    }

    if (showLocationSheet) {
        selectedLocation?.let { location ->
            LocationBottomSheet(
                onDismiss = { showLocationSheet = false },
                location = location,
                isGoogleMapsInstalled = isGoogleMapsInstalled,
                isWazeInstalled = isWazeInstalled,
                onItineraryClick = { navigationAppEnum ->
                    when (navigationAppEnum) {
                        NavigationAppEnum.WAZE -> {
                            val address = Uri.encode(location)

                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://waze.com/ul?q=$address&navigate=yes".toUri()
                            )

                            context.startActivity(intent)
                        }

                        NavigationAppEnum.GOOGLE_MAP -> {
                            val uri = "google.navigation:q=${Uri.encode(location)}".toUri()

                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            intent.setPackage("com.google.android.apps.maps")

                            if (intent.resolveActivity(packageManager) != null) {
                                context.startActivity(intent)
                            }
                        }
                    }
                },
                sheetState = sheetState
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = vacation?.vacation?.name
                            ?: stringResource(R.string.detailsscreen_error_title),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("detailsBackButton")
                    ) {
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
        DetailsScreenContent(
            vacationModel = vacation,
            onSharedVacationClick = { showSharedVacationSheet = true },
            onEditClick = onEditClick,
            onLocationClick = { location ->
                selectedLocation = location
                showLocationSheet = true
            },
            modifier = Modifier.padding(paddingValues)
        )
    }
}