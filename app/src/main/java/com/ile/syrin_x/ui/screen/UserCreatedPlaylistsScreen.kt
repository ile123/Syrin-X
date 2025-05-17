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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ile.syrin_x.data.model.usercreatedplaylist.UserCreatedPlaylist
import com.ile.syrin_x.ui.screen.common.AddOrEditPlaylistModal
import com.ile.syrin_x.ui.screen.common.BottomBarNavigationComponent
import com.ile.syrin_x.ui.screen.common.HeaderComponent
import com.ile.syrin_x.viewModel.PlaylistManagementViewModel

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
    val playlists by playlistManagementViewModel.userPlaylists.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            HeaderComponent(navHostController)
        },
        bottomBar = {
            BottomBarNavigationComponent(navHostController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddModal = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New playlist"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Text(
                    "Created Playlists",
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal
                )
            }

            items(
                items = playlists,
                key = { it.userCreatedPlaylistId }
            ) { playlist ->
                UserCreatedPlaylistRow(
                    playlist = playlist,
                    onClick = {
                        navHostController.navigate(
                            "user_created_playlist_details_screen/${playlist.userCreatedPlaylistId}"
                        )
                    },
                    onEdit = { playlistToEdit = playlist },
                    onDelete = {
                        playlistManagementViewModel.deletePlaylist(playlist.userCreatedPlaylistId)
                    }
                )
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

@Composable
fun UserCreatedPlaylistRow(
    playlist: UserCreatedPlaylist,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Rename playlist",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete playlist",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}