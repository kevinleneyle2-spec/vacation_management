package com.example.mviapp.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mviapp.R
import com.example.mviapp.home.viewmodel.HomeViewModel
import com.example.mviapp.navigation.AppDestinations

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    isLoading: Boolean,
    onNavigate: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val vacationUiState by viewModel.vacationState.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    )

    Scaffold(modifier = modifier) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background_home),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            if (vacationUiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = "loader-rotation")
                    val angle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2000, easing = LinearEasing)
                        ),
                        label = "loader-angle"
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "Loading",
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(angle)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (vacationUiState.vacations.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = paddingValues,
                        ) {
                            items(
                                items = vacationUiState.vacations,
                                key = { vacation -> vacation.id }
                            ) { vacation ->
                                VacationItem(
                                    viewModel = viewModel,
                                    vacationDto = vacation,
                                    onItemSelected = {
                                        onNavigate(AppDestinations.buildDetailsRoute(vacation.id))
                                    }
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            TextButton(
                                onClick = {
                                    onNavigate(AppDestinations.INIT_ROUTE)
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = colorResource(id = R.color.orange),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "CREATE YOUR NEXT VACATION",
                                    fontSize = 24.sp
                                )
                            }
                        }
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            TextButton(
                                onClick = {
                                    onNavigate(AppDestinations.INIT_ROUTE)
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    containerColor = colorResource(id = R.color.orange),
                                    contentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = "CREATE YOUR FIRST VACATION",
                                    fontSize = 24.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}