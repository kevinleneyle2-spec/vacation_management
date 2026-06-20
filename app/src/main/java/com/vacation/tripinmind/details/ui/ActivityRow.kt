package com.vacation.tripinmind.details.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.vacation.tripinmind.R
import com.vacation.tripinmind.data.local.model.Activity

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
                .clickable { onLocationClick(activity.activityLocation) }
        )

        if (activity.activityLocation.isNotEmpty()) {
            IconButton(
                onClick = { onLocationClick(activity.activityLocation) },
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .testTag("addressButton")
            ) {
                if (LocalInspectionMode.current) {
                    Image(
                        painter = painterResource(R.drawable.map_ico),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    AsyncImage(
                        model = R.drawable.map_ico,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}