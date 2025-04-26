package com.ile.syrin_x.ui.screen

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.viewModel.PlayerViewModel
import com.ile.syrin_x.viewModel.PlaylistManagementViewModel
import com.ile.syrin_x.viewModel.TrackDetailsViewModel

@Composable
fun UserCreatedPlaylistsScreen(
    navHostController: NavHostController,
    playlistManagementViewModel: PlaylistManagementViewModel = hiltViewModel()
) {

}