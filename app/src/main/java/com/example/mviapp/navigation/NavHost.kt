package com.example.mviapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.mviapp.details.ui.DetailsScreen
import com.example.mviapp.details.viewmodel.DetailsViewModel
import com.example.mviapp.home.ui.HomeScreen
import com.example.mviapp.home.viewmodel.HomeViewModel
import com.example.mviapp.vacation.ui.ActivitiesScreen
import com.example.mviapp.vacation.ui.InitScreen
import com.example.mviapp.vacation.viewmodel.InitViewModel

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
            val homeViewModel: HomeViewModel = hiltViewModel()

            HomeScreen(
                viewModel = homeViewModel,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = AppDestinations.DETAILS_ROUTE,
            arguments = listOf(
                navArgument("vacationId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val detailsViewModel: DetailsViewModel = hiltViewModel()

            DetailsScreen(
                viewModel = detailsViewModel,
                onBackClick = {
                    if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                        navController.popBackStack()
                    }
                },
                onEditClick = { route ->
                    navController.navigate(route)
                }
            )
        }

        navigation(startDestination = AppDestinations.INIT_ROUTE, route = "vacation_flow") {
            composable(AppDestinations.INIT_ROUTE) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("vacation_flow")
                }
                val viewModel: InitViewModel = hiltViewModel(parentEntry)

                InitScreen(
                    viewModel = viewModel,
                    onNavigate = { navController.navigate(AppDestinations.ACTIVITIES_ROUTE) },
                    onBackClick = {
                        if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable(AppDestinations.ACTIVITIES_ROUTE) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry("vacation_flow")
                }
                val viewModel: InitViewModel = hiltViewModel(parentEntry)

                ActivitiesScreen(
                    viewModel = viewModel,
                    onNavigate = { route ->
                        if (route == AppDestinations.HOME_ROUTE) {
                            navController.navigate(route) {
                                popUpTo(AppDestinations.HOME_ROUTE) { inclusive = true }
                            }
                        } else {
                            navController.navigate(route)
                        }
                    },
                    onBackClick = {
                        if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable(
                route = "activities/{vacationId}",
                arguments = listOf(navArgument("vacationId") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("vacationId")
                ActivitiesScreen(
                    viewModel = hiltViewModel(),
                    vacationId = id,
                    onNavigate = { route ->
                        if (route == AppDestinations.HOME_ROUTE) {
                            navController.navigate(route) {
                                popUpTo(AppDestinations.HOME_ROUTE) { inclusive = true }
                            }
                        } else {
                            navController.navigate(route)
                        }
                    },
                    onBackClick = {
                        if (backStackEntry.lifecycle.currentState == Lifecycle.State.RESUMED) {
                            navController.popBackStack()
                        }
                    }
                )
            }
        }
    }
}