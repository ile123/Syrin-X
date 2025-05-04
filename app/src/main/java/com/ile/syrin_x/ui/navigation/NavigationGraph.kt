package com.ile.syrin_x.ui.navigation

sealed class NavigationGraph(val route: String) {
    data object LoginScreen : NavigationGraph(route = "login_screen")
    data object RegisterScreen : NavigationGraph(route = "register_screen")
    data object HomeScreen : NavigationGraph(route = "home_screen")
    data object MusicSourceScreen : NavigationGraph(route = "music_source_screen")
    data object SearchNavGraph : NavigationGraph(route = "search_nav_graph")
    data object SearchScreen : NavigationGraph(route = "search_screen")
    data object SearchResultScreen: NavigationGraph(route = "search_result_screen")
    data object TrackDetailsScreen: NavigationGraph(route = "track_details_screen")
    data object PlaylistDetailsScreen: NavigationGraph(route = "playlist_details_screen")
    data object AlbumDetailsScreen: NavigationGraph(route = "album_details_screen")
    data object UserCreatedPlaylistScreen: NavigationGraph(route = "user_created_playlist_screen")
    data object UserCreatedPlaylistDetailsScreen: NavigationGraph(route = "user_created_playlist_details_screen")
    data object ProfileScreen: NavigationGraph(route = "profile_screen")
    data object PaymentScreen: NavigationGraph(route = "payment_screen")
}