package com.ile.syrin_x.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ile.syrin_x.ui.screen.HomeScreen
import com.ile.syrin_x.ui.screen.LoginScreen
import com.ile.syrin_x.ui.screen.MusicSourceScreen
import com.ile.syrin_x.ui.screen.RegisterScreen
import com.ile.syrin_x.ui.screen.SearchResultScreen
import com.ile.syrin_x.ui.screen.SearchScreen

@Composable
fun SetUpNavigationGraph(
    navHostController: NavHostController = rememberNavController(),
    authenticationNavigationViewModel: AuthenticationNavigationViewModel = hiltViewModel()
) {

    NavHost(
        navController = navHostController,
        startDestination = if (authenticationNavigationViewModel.isLoggedInState.value)
            NavigationGraph.LoginScreen.route
        else
            NavigationGraph.HomeScreen.route
    ) {
        composable(
            route = NavigationGraph.HomeScreen.route
        ) {
            HomeScreen(navHostController = navHostController)
        }
        composable(
            route = NavigationGraph.LoginScreen.route
        ) {
            LoginScreen(navHostController)
        }
        composable(
            route = NavigationGraph.RegisterScreen.route
        ) {
            RegisterScreen(navHostController)
        }
        composable(
            route = NavigationGraph.MusicSourceScreen.route
        ) {
            MusicSourceScreen()
        }
        composable(
            route = NavigationGraph.SearchScreen.route
        ) {
            SearchScreen(navHostController)
        }
        composable(
            route = NavigationGraph.SearchResultScreen.route
        ) {
            SearchResultScreen(navHostController)
        }
    }
}
