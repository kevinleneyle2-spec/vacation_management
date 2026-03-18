package com.example.mviapp.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.model.VacationDto
import com.example.mviapp.R
import com.example.mviapp.home.intent.VacationIntent
import com.example.mviapp.home.viewmodel.HomeViewModel

@Composable
fun VacationItem(
    viewModel: HomeViewModel,
    vacationDto: VacationDto,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(start = 16.dp, end = 16.dp)
            .testTag("vacationCard"),
        onClick = { onItemSelected(vacationDto.name) },
    ) {
        Row {
            Image(
                painter = painterResource(id = R.drawable.vacation_ico),
                contentDescription = "Image Button",
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(72.dp)
                    .align(Alignment.CenterVertically)
                    .testTag("homeFavoriteButton")

            )
            Box(
                modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp, end = 16.dp)

            ) {
                Text(
                    text = vacationDto.name,
                    fontSize = 24.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.weight(1f))

            Box(
                Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp)
            ) {
                Button(
                    onClick = { viewModel.handleIntent(VacationIntent.DeleteVacation(vacationDto)) },
                    modifier = Modifier
                        .size(72.dp)
                        .testTag("vacationDeleteButton") ,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.white)
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.trash_red),
                        contentDescription = "Image Button",
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
    }
}