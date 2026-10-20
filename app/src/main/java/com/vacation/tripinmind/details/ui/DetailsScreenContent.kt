package com.vacation.tripinmind.details.ui

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vacation.tripinmind.R
import com.vacation.tripinmind.details.model.VacationUiModel
import com.vacation.tripinmind.model.Activity
import com.vacation.tripinmind.model.Day
import com.vacation.tripinmind.model.Vacation
import com.vacation.tripinmind.ui.theme.MVIAppTheme
import java.util.Calendar

@Composable
fun DetailsScreenContent(
    vacationModel: VacationUiModel?,
    onSharedVacationClick: () -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onLocationClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.End

    ) {
        Row(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            vacationModel?.vacation?.let { currentVacation ->

                Button(
                    onClick = { onEditClick(currentVacation.id) },
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
                        .clickable { onEditClick(currentVacation.id) }
                )
            }
        }

        Row() {
            Button(
                onClick = { onSharedVacationClick() },
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
                    .clickable { onSharedVacationClick() }
            )
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize(),
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

                val initialPage = remember(currentVacation.days) {
                    findTodayPageIndex(currentVacation.days)
                }
                val pagerState = rememberPagerState(
                    initialPage = initialPage,
                    pageCount = { currentVacation.days.size }
                )

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
                            onLocationClick(location)
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

private fun findTodayPageIndex(days: List<Day>): Int {
    val today = Calendar.getInstance()

    return days.indexOfFirst { day ->
        val dayCalendar = Calendar.getInstance().apply {
            timeInMillis = day.date
        }

        today.get(Calendar.YEAR) == dayCalendar.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == dayCalendar.get(Calendar.DAY_OF_YEAR)
    }.let { index ->
        if (index >= 0) index else 0
    }
}


@Preview(showBackground = true)
@Composable
fun DetailsScreenContentPreview() {
    MVIAppTheme {
        Surface {
            DetailsScreenContent(
                vacationModel = VacationUiModel(
                    vacation = Vacation(
                        id = "1",
                        name = "Paris Trip",
                        days = listOf(
                            Day(
                                date = 1789862400000,
                                "Mercredi 5 mars 2025",
                                "with my father",
                                listOf(
                                    Activity("Eiffel Tower", "01h00", "02h00", ""),
                                    Activity("Eiffel Tower", "01h00", "02h00", ""),
                                    Activity("Louvre Museum", "04h30", "02h00", "")
                                )
                            ),
                            Day(
                                date = 1789862400000,
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
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenContentSharedVacationPreview() {
    MVIAppTheme {
        Surface {
            DetailsScreenContent(
                vacationModel = VacationUiModel(
                    vacation = Vacation(
                        id = "1",
                        name = "Paris Trip",
                        days = listOf(
                            Day(
                                date = 1789862400000,
                                "Day 1",
                                "father",
                                listOf()
                            ),
                            Day(
                                date = 1789862400000,
                                "Day 2",
                                "mother",
                                listOf()
                            )
                        ),
                        ideas = listOf()
                    )
                ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenContentEmptyPreview() {
    MVIAppTheme {
        Surface {
            DetailsScreenContent(
                vacationModel = VacationUiModel(
                    vacation = Vacation(
                        id = "1",
                        name = "Paris Trip",
                        days = listOf(
                            Day(
                                date = 1789862400000,
                                "Day 1",
                                "father",
                                listOf()
                            ),
                            Day(
                                date = 1789862400000,
                                "Day 2",
                                "mother",
                                listOf()
                            )
                        ),
                        ideas = listOf()
                    )
                ),
            )
        }
    }
}
