package com.ile.syrin_x.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.ui.screen.AlbumDetailsScreen
import com.ile.syrin_x.ui.screen.HomeScreen
import com.ile.syrin_x.ui.screen.LoginScreen
import com.ile.syrin_x.ui.screen.MusicSourceScreen
import com.ile.syrin_x.ui.screen.PlaylistDetailsScreen
import com.ile.syrin_x.ui.screen.RegisterScreen
import com.ile.syrin_x.ui.screen.SearchResultScreen
import com.ile.syrin_x.ui.screen.SearchScreen
import com.ile.syrin_x.ui.screen.TrackDetailsScreen
import com.ile.syrin_x.ui.screen.UserCreatedPlaylistsScreen
import com.ile.syrin_x.ui.screen.UserCreatedPlaylistDetailsScreen
import com.ile.syrin_x.viewModel.PlayerViewModel
import com.ile.syrin_x.viewModel.SearchViewModel

@Composable
fun SetUpNavigationGraph(
    playerViewModel: PlayerViewModel,
    navHostController: NavHostController = rememberNavController(),
    authenticationNavigationViewModel: AuthenticationNavigationViewModel = hiltViewModel(),
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
            route = NavigationGraph.UserCreatedPlaylistScreen.route
        ) {
            UserCreatedPlaylistsScreen(navHostController)
        }
        composable(
            route = NavigationGraph.UserCreatedPlaylistDetailsScreen.route
        ) {
            UserCreatedPlaylistDetailsScreen(playerViewModel, navHostController)
        }

        navigation(
            route = NavigationGraph.SearchNavGraph.route,
            startDestination = NavigationGraph.SearchScreen.route
        ) {
            composable(route = NavigationGraph.SearchScreen.route) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navHostController.getBackStackEntry(NavigationGraph.SearchNavGraph.route)
                }
                val searchViewModel: SearchViewModel = hiltViewModel(parentEntry)
                SearchScreen(navHostController, searchViewModel)
            }

            composable(route = NavigationGraph.SearchResultScreen.route) { navBackStackEntry ->
                val parentEntry = remember(navBackStackEntry) {
                    navHostController.getBackStackEntry(NavigationGraph.SearchNavGraph.route)
                }
                val searchViewModel: SearchViewModel = hiltViewModel(parentEntry)
                SearchResultScreen(navHostController, searchViewModel)
            }
        }

        composable(
            route = NavigationGraph.TrackDetailsScreen.route + "/{trackId}/{musicSource}",
            arguments = listOf(
                navArgument("trackId") { type = NavType.StringType },
                navArgument("musicSource") { type = NavType.StringType }
            )
        ) {
            val trackId = it.arguments?.getString("trackId") ?: ""
            val musicSourceString = it.arguments?.getString("musicSource") ?: ""

            val musicSource = MusicSource.valueOf(musicSourceString)

            TrackDetailsScreen(
                playerViewModel,
                navHostController = navHostController,
                trackId = trackId,
                musicSource = musicSource
            )
        }

        composable(
            route = NavigationGraph.PlaylistDetailsScreen.route + "/{playlistId}/{musicSource}",
            arguments = listOf(
                navArgument("playlistId") { type = NavType.StringType },
                navArgument("musicSource") { type = NavType.StringType }
            )
        ) {
            val playlistId = it.arguments?.getString("playlistId") ?: ""
            val musicSourceString = it.arguments?.getString("musicSource") ?: ""
            val musicSource = MusicSource.valueOf(musicSourceString)

            PlaylistDetailsScreen(
                playerViewModel,
                navHostController = navHostController,
                playlistId = playlistId,
                musicSource = musicSource
            )
        }

        composable(
            route = NavigationGraph.AlbumDetailsScreen.route + "/{albumId}/{musicSource}",
            arguments = listOf(
                navArgument("albumId") { type = NavType.StringType },
                navArgument("musicSource") { type = NavType.StringType }
            )
        ) {
            val albumId = it.arguments?.getString("albumId") ?: ""
            val musicSourceString = it.arguments?.getString("musicSource") ?: ""
            val musicSource = MusicSource.valueOf(musicSourceString)

            AlbumDetailsScreen(
                playerViewModel,
                navHostController,
                albumId,
                musicSource
            )
        }

    }
}

