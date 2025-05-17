package com.ile.syrin_x.ui.screen

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.UnifiedUser
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.screen.common.BottomBarNavigationComponent
import com.ile.syrin_x.ui.screen.common.HeaderComponent
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.utils.formatDuration
import com.ile.syrin_x.viewModel.ArtistDetailsViewModel
import com.ile.syrin_x.viewModel.PlayerViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@Composable
fun ArtistDetailsScreen(
    playerViewModel: PlayerViewModel,
    navHostController: NavHostController,
    artistId: String,
    musicSource: MusicSource,
    artistDetailsViewModel: ArtistDetailsViewModel = hiltViewModel()
) {
    val artistDetailsState by artistDetailsViewModel.searchFlow.collectAsState(initial = Response.Loading)
    val scope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }

    LaunchedEffect(artistId, musicSource) {
        artistDetailsViewModel.getArtistInfoAndSongs(artistId, musicSource)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { HeaderComponent(navHostController) },
        bottomBar = { BottomBarNavigationComponent(navHostController) },
        snackbarHost = { SnackbarHost(hostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = artistDetailsState) {
                is Response.Loading -> ArtistDetailsLoadingState()
                is Response.Error   -> ArtistDetailsErrorState(errorMessage = state.message)
                is Response.Success -> {
                    val artist = state.data as UnifiedUser
                    ArtistDetailsContent(
                        paddingValues = paddingValues,
                        searchFlowState = artistDetailsViewModel.searchFlow,
                        searchSuccess = { },
                        searchError = { msg ->
                            scope.launch { hostState.showSnackbar(msg) }
                        },
                        artistDetails = artist,
                        tracks = artistDetailsViewModel.tracksInAlbum,
                        playerViewModel = playerViewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun ArtistDetailsLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MyCircularProgress()
    }
}

@Composable
private fun ArtistDetailsErrorState(errorMessage: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun ArtistDetailsContent(
    paddingValues: PaddingValues,
    searchFlowState: SharedFlow<Response<Any>>,
    searchSuccess: () -> Unit,
    searchError: (error: String) -> Unit,
    artistDetails: UnifiedUser,
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
                    artistDetails.avatarUrl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = artistDetails.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = artistDetails.name ?: "Unknown Artist",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(8.dp))
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
                        Icon(Icons.Default.PlayArrow, contentDescription = "Play All")
                        Spacer(Modifier.width(8.dp))
                        Text("Play All")
                    }
                }
            }
        }

        item { Spacer(Modifier.height(4.dp)) }

        item {
            Text(
                text = "Top Tracks",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        if (tracks.isNotEmpty()) {
            items(tracks, key = { it.id }) { track ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { playerViewModel.playTrack(track) },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    ArtistTrackRow(
                        track = track,
                        modifier = Modifier.padding(12.dp),
                        playerViewModel = playerViewModel
                    )
                }
            }
        } else {
            item {
                Text("Artist/User dose not have any tracks that are available for playing.")
            }
        }
    }

    ArtistDetailsState(
        artistDetailsFlowState = searchFlowState,
        onSuccess = searchSuccess,
        onError = searchError
    )
}


@Composable
fun ArtistTrackRow(
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
                text = track.artists?.joinToString(", ") ?: "Unknown Artist",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        track.durationMs?.let {
            Text(
                text = formatDuration(it),
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
fun ArtistDetailsState(
    artistDetailsFlowState: SharedFlow<Response<Any>>,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        artistDetailsFlowState.collect {
            when (it) {
                is Response.Loading -> {
                    Log.i("ArtistDetailsState", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("ArtistDetailsState", it.message)
                    isLoading.value = false
                    onError(it.message)
                }

                is Response.Success -> {
                    Log.i("ArtistDetailsState", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}