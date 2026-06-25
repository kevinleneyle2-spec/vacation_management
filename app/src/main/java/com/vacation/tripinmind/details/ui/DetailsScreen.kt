package com.vacation.tripinmind.details.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.vacation.tripinmind.R
import com.vacation.tripinmind.data.local.model.Activity
import com.vacation.tripinmind.data.local.model.Day
import com.vacation.tripinmind.data.local.model.VacationDto
import com.vacation.tripinmind.details.intent.DetailsIntent
import com.vacation.tripinmind.details.model.NavigationAppEnum
import com.vacation.tripinmind.details.model.VacationUiModel
import com.vacation.tripinmind.details.viewmodel.DetailsViewModel
import com.vacation.tripinmind.navigation.AppDestinations
import com.vacation.tripinmind.ui.theme.MVIAppTheme

@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onBackClick: () -> Unit = {},
    onEditClick: (String) -> Unit = {}
) {
    val vacation by viewModel.vacation.collectAsState()

    DetailsScreenContent(
        vacationModel = vacation,
        onBackClick = onBackClick,
        onEditClick = onEditClick,
        sharedVacation = vacation?.isSharedVacation,
        onIntent = viewModel::handleIntent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreenContent(
    vacationModel: VacationUiModel?,
    onBackClick: () -> Unit = {},
    onEditClick: (String) -> Unit = {},
    sharedVacation: Boolean? = false,
    onIntent: (DetailsIntent) -> Unit = {}
) {
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
                        vacationDto = vacationModel?.vacation,
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
            vacationModel = vacationModel,
            error = vacationModel?.error,
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
                        text = vacationModel?.vacation?.name
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
        if (sharedVacation != true) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.End

            ) {
                Row(
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    vacationModel?.vacation?.let { currentVacation ->
                        Button(
                            onClick = { onEditClick(AppDestinations.buildEditRoute(currentVacation.id)) },
                            modifier = Modifier
                                .size(24.dp)
                                .testTag("detailsEditButton"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            contentPadding = PaddingValues(all = 0.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "Image Button",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = stringResource(R.string.detailsscreen_edit_button),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clickable {
                                    onEditClick(
                                        AppDestinations.buildEditRoute(
                                            currentVacation.id
                                        )
                                    )
                                }
                        )
                    }
                }

                Row() {
                    Button(
                        onClick = { showSharedVacationSheet = true },
                        modifier = Modifier
                            .size(24.dp)
                            .testTag("detailsShareButton"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        contentPadding = PaddingValues(all = 0.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.viewer_ico),
                            contentDescription = "Image Button",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Text(
                        text = stringResource(R.string.detailsscreen_share_button),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clickable { showSharedVacationSheet = true }
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center
        ) {
            vacationModel?.vacation?.let { currentVacation ->
                if (currentVacation.days.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.calendar_ico),
                            contentDescription = "calendar ico",
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    val pagerState = rememberPagerState(pageCount = { currentVacation.days.size })

                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        pageSpacing = 16.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 16.dp)
                    ) { page ->
                        DayCard(
                            day = currentVacation.days[page],
                            onLocationClick = { location ->
                                selectedLocation = location
                                showLocationSheet = true
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        Modifier
                            .height(10.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(currentVacation.days.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.secondary
                            }
                            Box(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(8.dp)
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.detailsscreen_error_no_days),
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                IdeaCard(
                    currentVacation.ideas,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.detailsscreen_loading_vacation))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    MVIAppTheme {
        Surface {
            DetailsScreenContent(
                vacationModel = VacationUiModel(
                    vacation = VacationDto(
                        id = "1",
                        name = "Paris Trip",
                        days = listOf(
                            Day(
                                "Lundi 5 mars 2025",
                                "with my father",
                                listOf(
                                    Activity("Eiffel Tower", "01h00", "02h00", ""),
                                    Activity("Eiffel Tower", "01h00", "02h00", ""),
                                    Activity("Louvre Museum", "04h30", "02h00", "")
                                )
                            ),
                            Day(
                                "Day 2",
                                "mother",
                                listOf(
                                    Activity("Notre Dame", "", "09:00", "2h00"),
                                    Activity("Seine River Cruise", "", "18:00", "2h00")
                                )
                            )
                        ),
                        ideas = listOf("piscine", "tennis")
                    )
                ),
                onBackClick = {},
                onEditClick = {},
                sharedVacation = false,
                onIntent = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenSharedVacationPreview() {
    MVIAppTheme {
        Surface {
            DetailsScreenContent(
                vacationModel = VacationUiModel(
                    vacation = VacationDto(
                        id = "1",
                        name = "Paris Trip",
                        days = listOf(
                            Day(
                                "Day 1",
                                "father",
                                listOf()
                            ),
                            Day(
                                "Day 2",
                                "mother",
                                listOf()
                            )
                        ),
                        ideas = listOf()
                    )
                ),
                onBackClick = {},
                onEditClick = {},
                sharedVacation = true,
                onIntent = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenEmptyPreview() {
    MVIAppTheme {
        Surface {
            DetailsScreenContent(
                vacationModel = VacationUiModel(
                    vacation = VacationDto(
                        id = "1",
                        name = "Paris Trip",
                        days = listOf(
                            Day(
                                "Day 1",
                                "father",
                                listOf()
                            ),
                            Day(
                                "Day 2",
                                "mother",
                                listOf()
                            )
                        ),
                        ideas = listOf()
                    )
                ),
                onBackClick = {},
                onEditClick = {},
                sharedVacation = false,
                onIntent = {}
            )
        }
    }
}
