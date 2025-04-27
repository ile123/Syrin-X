package com.ile.syrin_x.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ile.syrin_x.data.model.usercreatedplaylist.UserCreatedPlaylist
import com.ile.syrin_x.ui.screen.common.AddOrEditPlaylistModal
import com.ile.syrin_x.viewModel.PlaylistManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserCreatedPlaylistsScreen(
    navHostController: NavHostController,
    playlistManagementViewModel: PlaylistManagementViewModel = hiltViewModel()
) {
    var playlistToEdit by remember { mutableStateOf<UserCreatedPlaylist?>(null) }
    var showAddModal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        playlistManagementViewModel.getAllUsersPlaylists()
    }
    val playlists = playlistManagementViewModel.userPlaylists

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Playlists") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddModal = true }) {
                Icon(Icons.Default.Add, contentDescription = "New playlist")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = playlists,
                key = { it.userCreatedPlaylistId }
            ) { playlist ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = playlist.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                navHostController.navigate("playlistDetails/${playlist.userCreatedPlaylistId}")
                            }
                    )
                    IconButton(onClick = { playlistToEdit = playlist }) {
                        Icon(Icons.Default.Edit, contentDescription = "Rename playlist")
                    }
                    IconButton(onClick = {
                        playlistManagementViewModel.deletePlaylist(playlist.userCreatedPlaylistId)
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete playlist")
                    }
                }
                Divider()
            }
        }
    }

    if (showAddModal) {
        AddOrEditPlaylistModal(
            playlist = null,
            confirmButtonText = "Create",
            dismissButtonText = "Cancel",
            cancelable = true,
            onDismiss = { showAddModal = false }
        )
    }

    playlistToEdit?.let { pl ->
        AddOrEditPlaylistModal(
            playlist = pl,
            confirmButtonText = "Save",
            dismissButtonText = "Cancel",
            cancelable = true,
            onDismiss = { playlistToEdit = null }
        )
    }
}