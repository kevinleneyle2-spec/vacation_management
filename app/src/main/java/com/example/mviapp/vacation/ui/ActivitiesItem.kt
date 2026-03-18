package com.example.mviapp.vacation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.data.local.model.Day
import com.example.mviapp.R


@Composable
fun ActivitiesItem(
    day: Day,
    onNameChange: (String) -> Unit,
    onActivitiesChange: (String) -> Unit,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var newActivityValue by remember { mutableStateOf("") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .testTag("vacationCard"),
        onClick = { onItemSelected("test") },
    ) {
        Text(text = "Name Day")
        OutlinedTextField(
            value = day.nameDay,
            onValueChange = { newValue ->
                if (newValue.length <= 100 && !newValue.contains("\n")) {
                    onNameChange(newValue)
                }
            },
            label = { Text("Enter name of your day") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Activities")

        day.activity.forEach { activity ->
            Text(text = activity)
        }

        Row() {
            OutlinedTextField(
                value = newActivityValue,
                onValueChange = { newValue ->
                    newActivityValue = newValue
                },
                label = { Text("Enter your activity") },
                modifier = Modifier.weight(1f)
            )

            TextButton(
                onClick = {
                    if (newActivityValue.isNotBlank()) {
                        onActivitiesChange(newActivityValue)
                        newActivityValue = ""
                    }
                },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = colorResource(id = R.color.orange),
                    contentColor = Color.White
                )
            ) {
                Text("ADD")
            }
        }


    }
}