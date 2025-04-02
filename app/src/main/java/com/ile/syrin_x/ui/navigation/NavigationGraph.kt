package com.ile.syrin_x.ui.navigation

sealed class NavigationGraph(val route: String) {
    data object LoginScreen : NavigationGraph(route = "login_screen")
    data object RegisterScreen : NavigationGraph(route = "register_screen")
    data object HomeScreen : NavigationGraph(route = "home_screen")
    data object MusicSourceScreen : NavigationGraph(route = "music_source_screen")
    data object SearchScreen : NavigationGraph(route = "search_screen")
    data object SearchResultScreen: NavigationGraph(route = "search_result_screen")
}