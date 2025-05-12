package com.ile.syrin_x.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.ile.syrin_x.R
import com.ile.syrin_x.data.enums.MusicCategory
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedPlaylist
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.icon.PlayIcon
import com.ile.syrin_x.ui.screen.common.BottomBarNavigationComponent
import com.ile.syrin_x.ui.screen.common.HeaderComponent
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.utils.GlobalContext
import com.ile.syrin_x.utils.formatDuration
import com.ile.syrin_x.viewModel.PlayerViewModel
import com.ile.syrin_x.viewModel.PlaylistDetailsViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Composable
fun PlaylistDetailsScreen(
    playerViewModel: PlayerViewModel,
    navHostController: NavHostController,
    playlistId: String,
    musicSource: MusicSource,
    playlistDetailsViewModel: PlaylistDetailsViewModel = hiltViewModel()
) {
    val playlistDetailsState by playlistDetailsViewModel.searchFlow.collectAsState(initial = Response.Loading)
    val scope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }

    LaunchedEffect(playlistId, musicSource) {
        playlistDetailsViewModel.getPlaylistDetails(playlistId, musicSource)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { HeaderComponent(navHostController) },
        bottomBar = { BottomBarNavigationComponent(navHostController) },
        snackbarHost = { SnackbarHost(hostState = hostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = playlistDetailsState) {
                is Response.Loading -> LoadingState()
                is Response.Error -> ErrorState(errorMessage = state.message)
                is Response.Success -> {
                    playlistDetailsViewModel.playlistDetails?.let { playlist ->
                        PlaylistDetailsContent(
                            paddingValues = paddingValues,
                            searchFlowState = playlistDetailsViewModel.searchFlow,
                            searchSuccess = { /* no-op */ },
                            searchError = { err ->
                                scope.launch { hostState.showSnackbar(err) }
                            },
                            playlistDetails = playlist,
                            tracks = playlistDetailsViewModel.songsInPlaylist,
                            playerViewModel = playerViewModel
                        )
                    }
                }
            }
        }
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

@Composable
fun PlaylistDetailsContent(
    paddingValues: PaddingValues,
    searchFlowState: MutableSharedFlow<Response<Any>>,
    searchSuccess: () -> Unit,
    searchError: (error: String) -> Unit,
    playlistDetails: UnifiedPlaylist,
    tracks: List<UnifiedTrack>,
    playerViewModel: PlayerViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    playlistDetails.images?.firstOrNull()?.url?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = playlistDetails.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = playlistDetails.name ?: "Unknown Playlist",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(8.dp))
                    InfoRow(label = "By", value = playlistDetails.ownerName ?: "Unknown")
                    playlistDetails.description?.let { desc ->
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (tracks.isNotEmpty()) {
                                playerViewModel.setTrackListAndPlayTracks(tracks)
                                searchSuccess()
                            } else {
                                searchError("No tracks available")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play Playlist")
                        Spacer(Modifier.width(8.dp))
                        Text("Play Playlist")
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(4.dp))
        }
        item {
            Text(
                text = "Tracks",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )
        }
        items(tracks, key = { track -> "${track.id}-${MusicCategory.PLAYLISTS}" }) { track ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { playerViewModel.playTrack(track) },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                UnifiedPlaylistTrackRow(
                    track = track,
                    modifier = Modifier.padding(12.dp),
                    playerViewModel = playerViewModel
                )
            }
        }
    }

    PlaylistDetailsState(
        playlistDetailsFlowState = searchFlowState,
        onSuccess = searchSuccess,
        onError = searchError
    )
}

@Composable
fun UnifiedPlaylistTrackRow(
    track: UnifiedTrack,
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        AsyncImage(
            model = track.artworkUrl,
            contentDescription = "${track.title} artwork",
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title ?: "Unknown Title",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = when {
                    !track.albumName.isNullOrEmpty() -> track.albumName
                    !track.artists.isNullOrEmpty() -> track.artists.joinToString(", ")
                    else -> "Unknown Artist"
                },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        track.durationMs?.let { dur ->
            Text(
                text = formatDuration(dur),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = { playerViewModel.playTrack(track) }) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play Track")
        }
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
fun PlaylistDetailsState(
    playlistDetailsFlowState: MutableSharedFlow<Response<Any>>,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        playlistDetailsFlowState.collect {
            when (it) {
                is Response.Loading -> {
                    Log.i("PlaylistDetailsState", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("PlaylistDetailsState", it.message)
                    isLoading.value = false
                    onError(it.message)
                }

                is Response.Success -> {
                    Log.i("PlaylistDetailsState", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}