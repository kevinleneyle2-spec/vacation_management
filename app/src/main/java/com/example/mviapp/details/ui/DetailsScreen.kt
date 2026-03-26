package com.example.mviapp.details.ui

import android.widget.ImageButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mviapp.R
import com.example.mviapp.details.model.ActivityUiModel
import com.example.mviapp.details.model.DayUiModel
import com.example.mviapp.details.model.VacationUiModel
import com.example.mviapp.details.viewmodel.DetailsViewModel
import com.example.mviapp.ui.theme.MVIAppTheme

@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel,
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val vacation by viewModel.vacation.collectAsState()
    DetailsScreenContent(
        vacation = vacation,
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreenContent(
    vacation: VacationUiModel?,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = vacation?.name ?: stringResource(R.string.detailsscreen_error_title),
                        color = colorResource(R.color.white)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = colorResource(R.color.white)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorResource(id = R.color.orange)
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center
        ) {
            vacation?.let { currentVacation ->
                if (currentVacation.days.isNotEmpty()) {
                    val pagerState = rememberPagerState(pageCount = { currentVacation.days.size })

                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        pageSpacing = 16.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp)
                    ) { page ->
                        DayCard(day = currentVacation.days[page])
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
                                colorResource(R.color.deep_orange)
                            } else {
                                colorResource(R.color.orange)
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

                Button(
                    onClick = { },
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.CenterHorizontally)
                        .testTag("detailsEditButton") ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.white)
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Image Button",
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.detailsscreen_edit_button),
                    color = colorResource(R.color.orange),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.detailsscreen_loading_vacation))
            }
        }
    }
}

@Composable
fun DayCard(day: DayUiModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.white_orange_dark)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = day.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.brown)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (day.activities.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(day.activities) { activity ->
                        ActivityRow(activity)
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.detailsscreen_no_activities),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(R.color.brown)
                )
            }
        }
    }
}

@Composable
fun ActivityRow(activity: ActivityUiModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = colorResource(R.color.deep_orange)
        )
        Text(
            text = activity.time,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = colorResource(R.color.deep_orange)
        )
        Text(
            text = activity.name,
            style = MaterialTheme.typography.bodyMedium,
            color = colorResource(R.color.brown)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
    MVIAppTheme {
        Surface {
            DetailsScreenContent(
                vacation = VacationUiModel(
                    id = 1,
                    name = "Paris Trip",
                    days = listOf(
                        DayUiModel(
                            "Day 1",
                            listOf(
                                ActivityUiModel("Eiffel Tower", "10:00"),
                                ActivityUiModel("Louvre Museum", "14:30")
                            )
                        ),
                        DayUiModel(
                            "Day 2",
                            listOf(
                                ActivityUiModel("Notre Dame", "09:00"),
                                ActivityUiModel("Seine River Cruise", "18:00")
                            )
                        )
                    )
                )
            )
        }
    }
}
