package com.ile.syrin_x.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ile.syrin_x.viewModel.PlayerViewModel
import com.ile.syrin_x.viewModel.PlaylistManagementViewModel

@Composable
fun UserCreatedPlaylistDetailsScreen(
    playerViewModel: PlayerViewModel,
    navHostController: NavHostController,
    playlistManagementViewModel: PlaylistManagementViewModel = hiltViewModel(),
) {
    Text("Text")
}