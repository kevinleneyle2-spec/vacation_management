package com.vacation.tripinmind.home.ui

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.vacation.tripinmind.R
import com.vacation.tripinmind.home.model.VacationFilter
import com.vacation.tripinmind.home.model.VacationUiViewState
import com.vacation.tripinmind.model.Vacation
import com.vacation.tripinmind.ui.theme.MVIAppTheme

@Composable
fun HomeScreenContent(
    vacationUiState: VacationUiViewState,
    onDeleteVacation: (Vacation) -> Unit,
    onArchiveVacation: (Vacation) -> Unit,
    onToggleShowFilterVacations: (vacationFilter: VacationFilter) -> Unit,
    onShowQrcode: () -> Unit,
    onVacationDetailsClick: (String?, Boolean) -> Unit,
    onAddVacationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val projectsSize = remember(vacationUiState.vacations) {
        vacationUiState.vacations.count { !it.isArchived }
    }

    val archivedSize = remember(vacationUiState.vacations) {
        vacationUiState.vacations.count { it.isArchived }
    }

    val sharedSize = remember(vacationUiState.sharedVacations) {
        vacationUiState.sharedVacations.size
    }

    fun getSizeVacationFilter(vacationFilter: VacationFilter): Int {
        return when (vacationFilter) {
            VacationFilter.PROJECTS -> projectsSize
            VacationFilter.ARCHIVED -> archivedSize
            VacationFilter.SHARED -> sharedSize
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (LocalInspectionMode.current) {
            Image(
                painter = painterResource(id = R.drawable.background_home),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            AsyncImage(
                model = R.drawable.background_home,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (vacationUiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorResource(id = R.color.orange)
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .height(24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    if (vacationUiState.selectedFilter == VacationFilter.ARCHIVED)
                                        colorResource(R.color.orange)
                                    else
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                )
                                .clickable { onToggleShowFilterVacations(VacationFilter.ARCHIVED) }
                                .testTag("archivedVacationViewButton"),
                            contentAlignment = Alignment.Center

                        ) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Archive,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(18.dp),
                                    tint = Color.White
                                )

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    text = stringResource(
                                        R.string.homescreen_archives_button,
                                        getSizeVacationFilter(VacationFilter.ARCHIVED)
                                    ),
                                    color = Color.White,
                                    fontWeight = if (vacationUiState.selectedFilter == VacationFilter.ARCHIVED) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .padding(top = 12.dp, start = 32.dp, end = 32.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    if (vacationUiState.selectedFilter == VacationFilter.PROJECTS)
                                        colorResource(R.color.orange)
                                    else
                                        Color.Transparent
                                )
                                .clickable {
                                    if (vacationUiState.selectedFilter != VacationFilter.PROJECTS) onToggleShowFilterVacations(
                                        VacationFilter.PROJECTS
                                    )
                                }
                                .testTag("projectVacationViewButton"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.homescreen_projects_button,
                                    getSizeVacationFilter(VacationFilter.PROJECTS)
                                ),
                                color = Color.White,
                                fontWeight = if (vacationUiState.selectedFilter == VacationFilter.PROJECTS) FontWeight.Bold else FontWeight.Normal,
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
                                    if (vacationUiState.selectedFilter == VacationFilter.SHARED)
                                        colorResource(R.color.orange)
                                    else
                                        Color.Transparent
                                )
                                .clickable {
                                    if (vacationUiState.selectedFilter != VacationFilter.SHARED) onToggleShowFilterVacations(
                                        VacationFilter.SHARED
                                    )
                                }
                                .testTag("sharedVacationViewButton"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(
                                    R.string.homescreen_shared_button,
                                    getSizeVacationFilter(VacationFilter.SHARED)
                                ),
                                color = Color.White,
                                fontWeight = if (vacationUiState.selectedFilter == VacationFilter.SHARED) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 16.sp
                            )
                        }
                    }

                    var filteredVacations = vacationUiState.vacations

                    when (vacationUiState.selectedFilter) {
                        VacationFilter.PROJECTS -> {
                            filteredVacations = vacationUiState.vacations.filter {
                                !it.isArchived
                            }
                        }

                        VacationFilter.ARCHIVED -> {
                            filteredVacations = vacationUiState.vacations.filter {
                                it.isArchived
                            }
                        }

                        VacationFilter.SHARED -> {
                            filteredVacations = vacationUiState.sharedVacations
                        }
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
                                    onItemSelected = { vacationId, isShared ->
                                        onVacationDetailsClick(
                                            vacationId,
                                            isShared
                                        )
                                    },
                                    onArchiveClick = { onArchiveVacation(vacation) },
                                    filter = vacationUiState.selectedFilter
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(32.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                Text(
                                    text = if (vacationUiState.selectedFilter == VacationFilter.ARCHIVED) {
                                        stringResource(id = R.string.homescreen_no_archived_vacation)
                                    } else if (vacationUiState.selectedFilter == VacationFilter.PROJECTS) {
                                        stringResource(id = R.string.homescreen_no_vacation)
                                    } else {
                                        stringResource(id = R.string.homescreen_no_shared_vacation)
                                    },
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 18.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 16.dp, start = 32.dp, end = 32.dp),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(70.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            colorResource(id = R.color.orange)
                                        )
                                    )
                                )
                                .border(
                                    width = 2.dp,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.6f),
                                            Color.White.copy(alpha = 0.1f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .clickable { onAddVacationClick() }
                                .testTag("newVacationButton"),
                            contentAlignment = Alignment.Center,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.next_vacation_ico),
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = stringResource(R.string.homescreen_next_button),
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp,
                                    textAlign = TextAlign.Start,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.weight(1f, fill = false)
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { onShowQrcode() }
                        ) {
                            Text(
                                text = stringResource(R.string.homescreen_share_code_text) + vacationUiState.shareCode,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }

                        IconButton(
                            onClick = { onShowQrcode() },
                            enabled = vacationUiState.shareCode.isNotBlank(),
                            modifier = Modifier
                                .size(56.dp)
                                .background(Color.Transparent)
                                .testTag("qrcodeButton")
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.qrcode_ico),
                                contentDescription = "qrcode",
                                modifier = Modifier.size(40.dp)
                            )
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
                    Vacation(
                        id = "1",
                        name = "Paris",
                        startDate = 1683705600000L,
                        endDate = 1683964800000L,
                        days = emptyList(),
                        ideas = emptyList(),
                        image = "vacation_ico",
                        isArchived = false,
                        createdBy = "",
                        shareWith = listOf()
                    )
                ),
                isLoading = false,
                selectedFilter = VacationFilter.PROJECTS,
                shareCode = "123-456-123-456"
            ),
            onDeleteVacation = {},
            onArchiveVacation = {},
            onToggleShowFilterVacations = {},
            onShowQrcode = {},
            onVacationDetailsClick = { _, _ -> },
            onAddVacationClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenSharedPreview() {
    MVIAppTheme {
        HomeScreenContent(
            vacationUiState = VacationUiViewState(
                vacations = listOf(),
                sharedVacations = listOf(
                    Vacation(
                        id = "1",
                        name = "Paris",
                        startDate = 1683705600000L,
                        endDate = 1683964800000L,
                        days = emptyList(),
                        ideas = emptyList(),
                        image = "vacation_ico",
                        isArchived = false,
                        createdBy = "",
                        shareWith = listOf()
                    )
                ),
                isLoading = false,
                selectedFilter = VacationFilter.SHARED,
                shareCode = "123-456-123-456"
            ),
            onDeleteVacation = {},
            onArchiveVacation = {},
            onToggleShowFilterVacations = {},
            onShowQrcode = {},
            onVacationDetailsClick = { _, _ -> },
            onAddVacationClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenEmptyPreview() {
    MVIAppTheme {
        HomeScreenContent(
            vacationUiState = VacationUiViewState(
                vacations = listOf(
                ),
                isLoading = false,
                selectedFilter = VacationFilter.PROJECTS,
                shareCode = "123-456-123-456"
            ),
            onDeleteVacation = {},
            onArchiveVacation = {},
            onToggleShowFilterVacations = {},
            onShowQrcode = {},
            onVacationDetailsClick = { _, _ -> },
            onAddVacationClick = {}
        )
    }
}
