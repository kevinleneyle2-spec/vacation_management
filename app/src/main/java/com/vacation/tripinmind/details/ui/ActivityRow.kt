package com.vacation.tripinmind.details.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vacation.tripinmind.data.local.model.Activity
import com.vacation.tripinmind.ui.theme.MVIAppTheme
import com.vacation.tripinmind.vacation.intent.InitIntent
import com.vacation.tripinmind.vacation.model.VacationState
import com.vacation.tripinmind.vacation.ui.InitScreen
import com.vacation.tripinmind.vacation.ui.InitViewModelActions
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun ActivityRow(activity: Activity, onLocationClick: (String) -> Unit) {
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
            text = activity.activityName + " (" + activity.activityDuration + ")",
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
}

@Preview(showBackground = true)
@Composable
fun ActivityRowPreview() {
    MVIAppTheme {
        ActivityRow(
            activity = Activity(
                activityName = "Eiffel Tower",
                activityTime = "01h00",
                activityDuration = "02h00",
                activityLocation = "Paris"
            ),
            onLocationClick = {}
        )
    }
}