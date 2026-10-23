package com.vacation.tripinmind.details.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vacation.tripinmind.R
import com.vacation.tripinmind.model.Activity
import com.vacation.tripinmind.ui.theme.MVIAppTheme
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Composable
fun ActivityRow(activity: Activity, isLast: Boolean = false, onLocationClick: (String) -> Unit) {
    Column {
        if (activity.activityTravelTime != "00h00") {
            val travelStartTime = calculateTravelStartTime(activity)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsCar,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = travelStartTime,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = stringResource(R.string.detailsscreen_travel_text, activity.activityName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp)
        ) {
            Text(
                text = stringResource(R.string.detailsscreen_travel_duration_text, activity.activityTravelTime),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
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
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = activity.activityTime,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = activity.activityName,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration =
                    if (activity.activityLocation.isNotEmpty()) {
                        TextDecoration.Underline
                    } else {
                        TextDecoration.None
                    },
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clickable {
                        if (activity.activityLocation.isNotEmpty())
                            onLocationClick(activity.activityLocation)
                    }
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, bottom = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.detailsscreen_activity_duration_text, activity.activityDuration),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        if (!isLast) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

private fun calculateTravelStartTime(activity: Activity): String {
    val formatter = DateTimeFormatter.ofPattern("HH'h'mm")

    val activityTime = LocalTime.parse(activity.activityTime, formatter)
    val travelDuration = LocalTime.parse(activity.activityTravelTime, formatter)

    val travelStartTime = activityTime
        .minusHours(travelDuration.hour.toLong())
        .minusMinutes(travelDuration.minute.toLong())

    return travelStartTime.format(formatter)
}

@Preview(showBackground = true)
@Composable
fun ActivityRowPreview() {
    MVIAppTheme {
        ActivityRow(
            activity = Activity(
                activityName = "Eiffel Tower",
                activityTime = "11h00",
                activityDuration = "02h00",
                activityLocation = "Paris",
                activityTravelTime = "00h15"
            ),
            onLocationClick = {}
        )
    }
}