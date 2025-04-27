package com.ile.syrin_x.ui.screen

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.*
import com.ile.syrin_x.ui.screen.common.AddTrackToPlaylistModal

@Composable
fun TrackDetailsScreen(
    playerViewModel: PlayerViewModel,
    navHostController: NavHostController,
    trackId: String,
    musicSource: MusicSource,
    trackDetailsViewModel: TrackDetailsViewModel = hiltViewModel(),
    playlistManagementViewModel: PlaylistManagementViewModel = hiltViewModel()
) {
    LaunchedEffect(trackId, musicSource) {
        trackDetailsViewModel.getTrackDetails(trackId, musicSource)
        playlistManagementViewModel.getAllUserFavoriteTracks()
    }

    val trackDetailsState by trackDetailsViewModel.searchFlow.collectAsState(initial = Response.Loading)
    val favorites = playlistManagementViewModel.userFavoriteTracks

    var showAddPlaylistDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Image(
            painter = painterResource(id = R.drawable.background_image_1),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        when (val state = trackDetailsState) {
            is Response.Loading -> LoadingState()
            is Response.Error   -> ErrorState(errorMessage = state.message)
            is Response.Success -> {
                val track = trackDetailsViewModel.trackDetails
                Content(
                    paddingValues = paddingValues,
                    track = track,
                    playerViewModel = playerViewModel,
                    onAddToPlaylist = { showAddPlaylistDialog = true },
                    onToggleFavorite = {
                        track?.let { playlistManagementViewModel.addOrRemoveTrackFromFavorites(it) }
                    },
                    isFavorite = track != null && favorites.any { it.favoriteTrackId == track.id }
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

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        MyCircularProgress()
    }
}

@Composable
fun ErrorState(errorMessage: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Error: $errorMessage", color = Color.Red)
    }
}

@Composable
private fun Content(
    paddingValues: PaddingValues,
    track: UnifiedTrack?,
    playerViewModel: PlayerViewModel,
    onAddToPlaylist: () -> Unit,
    onToggleFavorite: () -> Unit,
    isFavorite: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        track?.artworkUrl?.let { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(16.dp))

        IconButton(onClick = { track?.let { playerViewModel.playTrack(it) } }) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                modifier = Modifier.size(30.dp)
            )
        }

        Text(
            text = track?.title.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        track?.albumName?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        track?.artists?.let {
            Text(
                text = it.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        track?.genre?.let {
            Text(
                text = "Genre: $it",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        track?.durationMs?.let {
            Text(
                text = "Duration: ${formatDuration(it)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
        Text(
            text = if (track?.explicit == true) "Explicit Content" else "Clean Content",
            style = MaterialTheme.typography.bodyMedium,
            color = if (track?.explicit == true) Color.Red else Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        track?.popularity?.let {
            Text(
                text = "Popularity: $it",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            IconButton(onClick = onAddToPlaylist) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add to Playlist"
                )
            }
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Toggle Favorite",
                    tint = if (isFavorite) Color.Red else LocalContentColor.current
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        track?.playbackUrl?.let {
            Text(
                text = "Playback URL: $it",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}