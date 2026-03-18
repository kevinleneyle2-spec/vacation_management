package com.example.mviapp.vacation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mviapp.vacation.intent.InitIntent
import com.example.mviapp.vacation.viewmodel.InitViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  InitScreen(
    viewModel: InitViewModel,
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initState by viewModel.initState.collectAsState()
    val initValidation by viewModel.initValidation.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Initialization of your vacation") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Name of vacation")
            }

            OutlinedTextField(
                value = initState.vacationName,
                onValueChange = { newValue ->
                    if (newValue.length <= 100 && !newValue.contains("\n")) {
                        viewModel.handleIntent(InitIntent.UpdateName(newValue))
                    }
                },
                label = { Text("Enter name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(64.dp))

            Text(text = "Number of days")
            OutlinedTextField(
                value = if (initState.numDays == 0) "" else initState.numDays.toString(),
                onValueChange = { newValue ->
                    viewModel.handleIntent(InitIntent.UpdateDays(newValue))
                },
                label = { Text("Enter number between 0-15") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(64.dp))

            if (initValidation) {
                TextButton(
                    onClick = {
                        viewModel.handleIntent(InitIntent.CreateVacation(
                            vacationDto = initState.toVacationDto()
                        ))
                        onNavigate("activities")
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "NEXT",
                        fontSize = 24.sp
                    )
                }
            }
        }
    }
}