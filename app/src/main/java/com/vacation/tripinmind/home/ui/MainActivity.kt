package com.vacation.tripinmind.home.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.vacation.tripinmind.navigation.AppDestinations
import com.vacation.tripinmind.navigation.AppNavHost
import com.vacation.tripinmind.ui.theme.MVIAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        initSplashScreen()

        super.onCreate(savedInstanceState)

        setContent {
            MVIAppTheme {
                val navController = rememberNavController()
                AppNavHost(
                    navController = navController,
                    startDestination = AppDestinations.HOME_ROUTE
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            guestAuth()
        }
    }

    private fun initSplashScreen() {
        val splashScreen = installSplashScreen()

        var keepOnScreen = true
        splashScreen.setKeepOnScreenCondition { keepOnScreen }

        lifecycleScope.launch {
            delay(2000)
            keepOnScreen = false
        }
    }

    private fun guestAuth() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // TODO : Analytics
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