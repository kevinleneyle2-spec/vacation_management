package com.vacation.tripinmind.details.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vacation.tripinmind.R
import com.vacation.tripinmind.model.Activity
import com.vacation.tripinmind.model.Day
import com.vacation.tripinmind.ui.theme.MVIAppTheme

@Composable
fun DayCard(
    day: Day,
    onLocationClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = day.nameDay,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (day.additionalInfo.isNotEmpty()) {
                Text(
                    text = day.additionalInfo,
                    style = MaterialTheme.typography.titleMedium,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (day.activity.isNotEmpty()) {
                val sortedActivities = day.activity.sortedBy { it.activityTime }
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(sortedActivities) { index, activity ->
                        val isLast = index == sortedActivities.size - 1
                        ActivityRow(activity, isLast = isLast, onLocationClick = onLocationClick)
                    }
                }
            } else {
                Text(
                    text = stringResource(R.string.detailsscreen_no_activities),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DayCardPreview() {
    MVIAppTheme {
        Surface {
            DayCard(
                day = Day(
                    date = 1789862400000,
                    nameDay = "Lundi 5 mars 2025",
                    additionalInfo = "with my father",
                    activity = listOf(
                        Activity("Eiffel Tower", "01h00", "02h00", "", "01H00")
                    )
                ),
                onLocationClick = { }
            )
        }
    }
}