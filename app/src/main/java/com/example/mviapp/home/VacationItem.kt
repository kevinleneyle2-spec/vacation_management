package com.example.mviapp.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.model.VacationDto
import com.example.mviapp.R
import com.example.mviapp.ui.theme.MVIAppTheme

@Composable
fun VacationItem(
    vacationDto: VacationDto,
    onItemSelected: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onArchiveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconRes = when (vacationDto.image) {
        "beach_ico" -> R.drawable.beach_ico
        "ski_ico" -> R.drawable.ski_ico
        "forest_ico" -> R.drawable.forest_ico
        "plane_ico" -> R.drawable.plane_ico
        else -> R.drawable.vacation_ico
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(horizontal = 16.dp)
            .testTag("vacationCard"),
        onClick = { onItemSelected(vacationDto.name) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.Transparent, CircleShape)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
            }

            Box(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = vacationDto.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = { onArchiveClick() },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .testTag("vacationArchiveButton")
            ) {
                Image(
                    painter =
                        if (vacationDto.isArchived)
                            painterResource(id = R.drawable.unarchived)
                        else
                            painterResource(id = R.drawable.archive),
                    contentDescription = "Archive",
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(
                onClick = { onDeleteClick() },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent)
                    .testTag("vacationDeleteButton")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.trash_red),
                    contentDescription = "Delete",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VacationItemPreview() {
    MVIAppTheme {
        Box(modifier = Modifier.padding(vertical = 16.dp)) {
            VacationItem(
                vacationDto = VacationDto(
                    id = 1,
                    name = "Summer in Paris",
                    nbrDay = 5,
                    days = emptyList(),
                    ideas = emptyList(),
                    image = "vacation_ico",
                    isArchived = false
                ),
                onItemSelected = {},
                onDeleteClick = {},
                onArchiveClick = {}
            )
        }
    }
}
