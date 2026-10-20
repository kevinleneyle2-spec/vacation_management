package com.vacation.tripinmind.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.vacation.tripinmind.details.ui.DetailsScreen
import com.vacation.tripinmind.home.ui.HomeScreen
import com.vacation.tripinmind.vacation.ui.ActivitiesScreen
import com.vacation.tripinmind.vacation.ui.InitScreen
import com.vacation.tripinmind.vacation.viewmodel.InitViewModel
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object InitRoute

@Serializable
object ActivitiesRoute

@Serializable
data class DetailsRoute(val vacationId: String?, val sharedVacation: Boolean)

@Serializable
data class EditRoute(val vacationId: String?)

@Serializable
object VacationCreationGraph

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(
                onVacationDetailsClick = { vacationId, isShared ->
                    navController.navigate(DetailsRoute(vacationId, isShared))
                },
                onAddVacationClick = {
                    navController.navigate(InitRoute)
                }
            )
        }

        navigation<VacationCreationGraph>(startDestination = InitRoute) {
            composable<InitRoute> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<VacationCreationGraph>()
                }
                val sharedViewModel: InitViewModel = hiltViewModel(parentEntry)

                InitScreen(
                    viewModel = sharedViewModel,
                    onActivitiesClick = { navController.navigate(ActivitiesRoute) },
                    onBackClick = navController::popBackStack
                )
            }

            composable<ActivitiesRoute> { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry<VacationCreationGraph>()
                }
                val sharedViewModel: InitViewModel = hiltViewModel(parentEntry)

                ActivitiesScreen(
                    viewModel = sharedViewModel,
                    onHomeClick = {
                        navController.popBackStack<VacationCreationGraph>(inclusive = true)
                    },
                    onBackClick = navController::popBackStack
                )
            }
        }

        composable<DetailsRoute> {
            DetailsScreen(
                onBackClick = navController::popBackStack,
                onEditClick = { vacationId ->
                    navController.navigate(EditRoute(vacationId))
                }
            )
        }

        composable<EditRoute> { backStackEntry ->
            val editRoute = backStackEntry.toRoute<EditRoute>()
            ActivitiesScreen(
                vacationId = editRoute.vacationId,
                onHomeClick = { navController.navigate(HomeRoute) },
                onBackClick = navController::popBackStack
            )
        }
    }
}