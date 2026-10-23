package com.vacation.tripinmind.vacation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.vacation.tripinmind.R
import com.vacation.tripinmind.ui.theme.MVIAppTheme

@Composable
fun ActivityDialog(
    title: String,
    nameValue: String,
    onNameChange: (String) -> Unit,
    addressValue: String,
    onAddressChange: (String) -> Unit,
    timeValue: String,
    onTimeClick: () -> Unit,
    travelTimeValue: String,
    onTravelTimeClick: () -> Unit,
    durationValue: String,
    onDurationClick: () -> Unit,
    checkAddAllowed: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.orange)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = nameValue,
                    onValueChange = onNameChange,
                    trailingIcon = {
                        if (nameValue.isNotEmpty()) {
                            IconButton(onClick = { onNameChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear activity name",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    label = { Text(stringResource(R.string.activitiesscreen_activities_description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activityNameTextField"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = addressValue,
                    onValueChange = onAddressChange,
                    trailingIcon = {
                        if (addressValue.isNotEmpty()) {
                            IconButton(onClick = { onAddressChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear address activity",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    label = { Text(stringResource(R.string.activitiesscreen_address_activities)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activityAddressTextField"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTimeClick() }) {
                        OutlinedTextField(
                            value = timeValue,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text(stringResource(R.string.activitiesscreen_time_title)) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.primary,
                                disabledLabelColor = MaterialTheme.colorScheme.primary,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("activityTimeTextField"),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onDurationClick() }) {
                        OutlinedTextField(
                            value = durationValue,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text(stringResource(R.string.activitiesscreen_duration_title)) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.primary,
                                disabledLabelColor = MaterialTheme.colorScheme.primary,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("activityDurationTextField"),
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onTravelTimeClick() }) {
                        OutlinedTextField(
                            value = travelTimeValue,
                            onValueChange = {},
                            readOnly = true,
                            enabled = false,
                            label = { Text(stringResource(R.string.activitiesscreen_travel_time_title)) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.primary,
                                disabledLabelColor = MaterialTheme.colorScheme.primary,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("activityTravelTimeTextField"),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.common_cancel_button))
                    }
                    TextButton(
                        onClick = onConfirm,
                        enabled = checkAddAllowed
                    ) {
                        Text(stringResource(R.string.activitiesscreen_add_button))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActivityDialogPreview() {
    MVIAppTheme {
        ActivityDialog(
            title = "Ajouter une activité",
            nameValue = "Visite guidée",
            onNameChange = {},
            addressValue = "Paris",
            onAddressChange = {},
            timeValue = "14h00",
            onTimeClick = {},
            travelTimeValue = "01h00",
            onTravelTimeClick = {},
            durationValue = "02h00",
            onDurationClick = {},
            checkAddAllowed = true,
            onDismiss = {},
            onConfirm = {}
        )
    }
}