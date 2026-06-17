package com.vacation.tripinmind.home.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.vacation.tripinmind.navigation.AppDestinations
import com.vacation.tripinmind.navigation.AppNavHost
import com.vacation.tripinmind.ui.theme.MVIAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MVIAppTheme() {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    startDestination = AppDestinations.HOME_ROUTE
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MVIAppTheme {
        Greeting("Android")
    }
}