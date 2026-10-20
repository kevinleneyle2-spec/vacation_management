package com.vacation.tripinmind.vacation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vacation.tripinmind.R
import com.vacation.tripinmind.ui.theme.MVIAppTheme
import com.vacation.tripinmind.vacation.model.VacationState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@Composable
fun InitScreenContent(
    initState: VacationState,
    initValidation: Boolean,
    onUpdateName: (String) -> Unit,
    onSelectStartDateClick: () -> Unit,
    onSelectEndDateClick: () -> Unit,
    onUpdateImage: (String) -> Unit,
    onActivitiesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEndDateEnabled = initState.startDate > 0L

    val icons = listOf(
        "vacation_ico" to R.drawable.vacation_ico,
        "beach_ico" to R.drawable.beach_ico,
        "ski_ico" to R.drawable.ski_ico,
        "forest_ico" to R.drawable.forest_ico,
        "plane_ico" to R.drawable.plane_ico
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.initscreen_name_title),
            color = colorResource(R.color.orange),
            modifier = Modifier.align(Alignment.Start)
        )

        OutlinedTextField(
            value = initState.vacationName,
            onValueChange = { newValue ->
                val truncated = if (newValue.length > 25) newValue.take(25) else newValue
                if (!truncated.contains("\n")) {
                    onUpdateName(truncated)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = colorResource(R.color.orange),
                focusedBorderColor = colorResource(R.color.orange),
                unfocusedBorderColor = colorResource(R.color.orange),
                focusedLabelColor = colorResource(R.color.orange),
                unfocusedLabelColor = colorResource(R.color.orange)
            ),
            trailingIcon = {
                if (initState.vacationName.isNotEmpty()) {
                    IconButton(onClick = { onUpdateName("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear vacation name",
                            tint = colorResource(R.color.orange)
                        )
                    }
                }
            },
            label = {
                Text(
                    stringResource(R.string.initscreen_name_description),
                    color = colorResource(R.color.orange)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("initNameTextField"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.initscreen_date_title),
            color = colorResource(R.color.orange),
            modifier = Modifier.align(Alignment.Start)
        )
        val formattedStartDate = if (initState.startDate > 0L) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", LocalLocale.current.platformLocale).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            sdf.format(Date(initState.startDate))
        } else ""

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectStartDateClick() }
        ) {
            OutlinedTextField(
                value = formattedStartDate,
                onValueChange = { },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = colorResource(R.color.orange),
                    disabledLabelColor = colorResource(R.color.orange),
                    disabledTrailingIconColor = colorResource(R.color.orange),
                    disabledPlaceholderColor = colorResource(R.color.orange),
                    disabledContainerColor = Color.Transparent
                ),
                label = {
                    Text(
                        stringResource(R.string.initscreen_date_description),
                        color = colorResource(R.color.orange)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { onSelectStartDateClick() }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Start Date",
                            tint = colorResource(R.color.orange)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectStartDateClick() }
                    .testTag("initStartDateTextField"),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        val orangeColor = colorResource(R.color.orange)
        val endFieldColor = if (isEndDateEnabled) orangeColor else orangeColor.copy(alpha = 0.4f)

        Text(
            text = stringResource(R.string.initscreen_end_date_title),
            color = endFieldColor,
            modifier = Modifier.align(Alignment.Start)
        )
        val formattedEndDate = if (initState.endDate > 0L) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", LocalLocale.current.platformLocale).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            sdf.format(Date(initState.endDate))
        } else ""

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isEndDateEnabled) Modifier.clickable { onSelectEndDateClick() }
                    else Modifier
                )
        ) {
            OutlinedTextField(
                value = formattedEndDate,
                onValueChange = { },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = if (isEndDateEnabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.38f
                    ),
                    disabledBorderColor = endFieldColor,
                    disabledLabelColor = endFieldColor,
                    disabledTrailingIconColor = endFieldColor,
                    disabledPlaceholderColor = endFieldColor,
                    disabledContainerColor = Color.Transparent
                ),
                label = {
                    Text(
                        stringResource(R.string.initscreen_end_date_description),
                        color = endFieldColor
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { if (isEndDateEnabled) onSelectEndDateClick() },
                        enabled = isEndDateEnabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select End Date",
                            tint = endFieldColor
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isEndDateEnabled) Modifier.clickable { onSelectEndDateClick() }
                        else Modifier
                    )
                    .testTag("initEndDateTextField"),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.initscreen_choose_icon_text),
            color = colorResource(R.color.orange),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            icons.forEach { (name, resId) ->
                val isSelected = initState.image == name
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) colorResource(R.color.orange).copy(alpha = 0.2f)
                            else Color.Transparent
                        )
                        .border(
                            width = 2.dp,
                            color = if (isSelected) colorResource(R.color.orange)
                            else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { onUpdateImage(name) }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        TextButton(
            onClick = { onActivitiesClick() },
            enabled = initValidation,
            modifier = Modifier
                .align(Alignment.End)
                .testTag("initNextButton")
        ) {
            Text(
                text = stringResource(R.string.initscreen_next_button),
                fontSize = 24.sp,
                color = if (initValidation)
                    colorResource(R.color.orange)
                else
                    Color.Gray.copy(alpha = 0.2f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InitScreenContentPreview() {
    MVIAppTheme {
        InitScreenContent(
            initState = VacationState(vacationName = "Paris"),
            initValidation = true,
            onUpdateName = {},
            onSelectStartDateClick = {},
            onSelectEndDateClick = {},
            onUpdateImage = {},
            onActivitiesClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InitScreenContentEmptyPreview() {
    MVIAppTheme {
        InitScreenContent(
            initState = VacationState(),
            initValidation = false,
            onUpdateName = {},
            onSelectStartDateClick = {},
            onSelectEndDateClick = {},
            onUpdateImage = {},
            onActivitiesClick = {}
        )
    }
}
