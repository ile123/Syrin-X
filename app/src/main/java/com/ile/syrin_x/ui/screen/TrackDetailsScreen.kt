package com.ile.syrin_x.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.ile.syrin_x.R
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.viewModel.TrackDetailsViewModel
import com.ile.syrin_x.viewModel.PlayerViewModel
import com.ile.syrin_x.viewModel.PlaylistManagementViewModel

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.ile.syrin_x.data.model.usercreatedplaylist.FavoriteTrack
import com.ile.syrin_x.ui.screen.common.AddTrackToPlaylistModal
import com.ile.syrin_x.ui.screen.common.BottomBarNavigationComponent
import com.ile.syrin_x.ui.screen.common.HeaderComponent
import com.ile.syrin_x.utils.formatDuration

@Composable
fun TrackDetailsScreen(
    playerViewModel: PlayerViewModel,
    navHostController: NavHostController,
    trackId: String,
    musicSource: MusicSource,
    trackDetailsViewModel: TrackDetailsViewModel = hiltViewModel(),
    playlistManagementViewModel: PlaylistManagementViewModel = hiltViewModel()
) {
    val favorites by playlistManagementViewModel.favorites.collectAsState()
    var showAddPlaylistDialog by remember { mutableStateOf(false) }

    LaunchedEffect(trackId, musicSource) {
        trackDetailsViewModel.getTrackDetails(trackId, musicSource)
        playlistManagementViewModel.getAllUserFavoriteTracks()
        playlistManagementViewModel.getAllUsersPlaylists()
    }

    val trackDetailsState by trackDetailsViewModel.searchFlow.collectAsState(initial = Response.Loading)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HeaderComponent(navHostController)
        },
        bottomBar = {
            BottomBarNavigationComponent(navHostController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (trackDetailsState) {
                is Response.Loading -> LoadingState()
                is Response.Error -> ErrorState((trackDetailsState as Response.Error).message)
                is Response.Success -> {
                    trackDetailsViewModel.trackDetails?.let { track ->
                        TrackContent(
                            track = track,
                            favorites = favorites,
                            playerViewModel = playerViewModel,
                            onAddToPlaylist = { showAddPlaylistDialog = true },
                            onToggleFavorite = {
                                playlistManagementViewModel.addOrRemoveTrackFromFavorites(
                                    track
                                )
                            }
                        )
                    }
                }
            }

            if (showAddPlaylistDialog) {
                trackDetailsViewModel.trackDetails?.let { track ->
                    AddTrackToPlaylistModal(
                        track = track,
                        onDismiss = { showAddPlaylistDialog = false },
                        title = "Add to Playlist",
                        confirmButtonText = "Done",
                        dismissButtonText = "Cancel",
                        cancelable = true
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackContent(
    track: UnifiedTrack,
    favorites: List<FavoriteTrack>,
    playerViewModel: PlayerViewModel,
    onAddToPlaylist: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val isFavorite = favorites.any { it.favoriteTrackId == track.id }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = track.artworkUrl,
            contentDescription = "Cover art",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            IconButton(
                onClick = { playerViewModel.playTrack(track) },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(
                onClick = onAddToPlaylist,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to Playlist",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = track.title ?: "Unknown Title",
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = listOfNotNull(
                        track.albumName,
                        track.artists?.joinToString(", ")
                    ).joinToString(" • "),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                InfoRow(
                    label = "Genre",
                    value = track.genre.takeIf { it?.isNotBlank() == true } ?: "Unknown")
                InfoRow(label = "Duration", value = formatDuration(track.durationMs ?: 0))
                InfoRow(
                    label = "Content",
                    value = if (track.explicit == true) "Explicit" else "Clean",
                    valueColor = if (track.explicit == true) Color.Red else MaterialTheme.colorScheme.onSurface
                )
                track.popularity?.let {
                    InfoRow(label = "Popularity", value = it.toString())
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(text = "$label:", style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, color = valueColor)
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MyCircularProgress()
    }
}

@Composable
private fun ErrorState(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
    }
}
