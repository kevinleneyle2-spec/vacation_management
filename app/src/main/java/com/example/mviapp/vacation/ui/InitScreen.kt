package com.example.mviapp.vacation.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mviapp.R
import com.example.mviapp.ui.theme.MVIAppTheme
import com.example.mviapp.vacation.intent.InitIntent
import com.example.mviapp.vacation.intent.VacationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface InitViewModelActions {
    val initState: StateFlow<VacationState>
    val initValidation: StateFlow<Boolean>
    fun handleIntent(intent: InitIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitScreen(
    viewModel: InitViewModelActions,
    onNavigate: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val initState by viewModel.initState.collectAsStateWithLifecycle()
    val initValidation by viewModel.initValidation.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.initscreen_title),
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.initscreen_name_title),
                    color = colorResource(R.color.orange)
                )
            }

            OutlinedTextField(
                value = initState.vacationName,
                onValueChange = { newValue ->
                    if (newValue.length <= 100 && !newValue.contains("\n")) {
                        viewModel.handleIntent(InitIntent.UpdateName(newValue))
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = colorResource(R.color.orange),
                    focusedBorderColor = colorResource(R.color.orange),
                    unfocusedBorderColor = colorResource(R.color.orange),
                    focusedLabelColor = colorResource(R.color.orange),
                    unfocusedLabelColor = colorResource(R.color.orange)
                ),
                label = {
                    Text(
                        stringResource(R.string.initscreen_name_description),
                        color = colorResource(R.color.orange)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(64.dp))

            Text(
                text = stringResource(R.string.initscreen_day_title),
                color = colorResource(R.color.orange)
            )
            OutlinedTextField(
                value = if (initState.numDays == 0) "" else initState.numDays.toString(),
                onValueChange = { newValue ->
                    viewModel.handleIntent(InitIntent.UpdateDays(newValue))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    cursorColor = colorResource(R.color.orange),
                    focusedBorderColor = colorResource(R.color.orange),
                    unfocusedBorderColor = colorResource(R.color.orange),
                    focusedLabelColor = colorResource(R.color.orange),
                    unfocusedLabelColor = colorResource(R.color.orange)
                ),
                label = {
                    Text(
                        stringResource(R.string.initscreen_day_description),
                        color = colorResource(R.color.orange)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
            )

            Spacer(modifier = Modifier.height(64.dp))

            if (initValidation) {
                TextButton(
                    onClick = {
                        onNavigate("activities")
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = stringResource(R.string.initscreen_next_button),
                        fontSize = 24.sp,
                        color = colorResource(id = R.color.orange)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InitScreenPreview() {
    val mockActions = object : InitViewModelActions {
        override val initState = MutableStateFlow(
            VacationState(vacationName = "Summer Trip", numDays = 5)
        )
        override val initValidation = MutableStateFlow(true)
        override fun handleIntent(intent: InitIntent) {}
    }

    MVIAppTheme {
        InitScreen(viewModel = mockActions)
    }
}

@Preview(showBackground = true)
@Composable
fun InitScreenEmptyPreview() {
    val mockActions = object : InitViewModelActions {
        override val initState = MutableStateFlow(VacationState())
        override val initValidation = MutableStateFlow(false)
        override fun handleIntent(intent: InitIntent) {}
    }

    MVIAppTheme {
        InitScreen(viewModel = mockActions)
    }
}
