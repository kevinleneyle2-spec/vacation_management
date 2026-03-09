package com.example.mviapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mviapp.home.HomeScreen
import com.example.mviapp.home.viewmodel.HomeViewModel

@Composable
fun AppNavHost(
    navController: androidx.navigation.NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AppDestinations.HOME_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = AppDestinations.HOME_ROUTE) {
            val homeViewModel = hiltViewModel<HomeViewModel>()

            HomeScreen(
                viewModel = homeViewModel,
                isLoading = false,
                onItemSelected = { selectedAlbum ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("album", selectedAlbum)
                    navController.navigate(AppDestinations.DETAILS_ROUTE)
                }
            )
        }
    }
}