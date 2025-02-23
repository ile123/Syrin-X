package com.ile.syrin_x

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ile.syrin_x.composition.LocalNavController
import com.ile.syrin_x.screen.HomeScreen
import com.ile.syrin_x.screen.LoginScreen
import com.ile.syrin_x.screen.RegisterScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()
            CompositionLocalProvider(LocalNavController provides navController) {
                NavHost(navController = navController, startDestination = "HomeScreen") {
                    composable("HomeScreen") {
                        HomeScreen()
                    }
                    composable("LoginScreen") {
                        LoginScreen()
                    }
                    composable("RegisterScreen") {
                        RegisterScreen()
                    }
                }
            }
        }
    }
}