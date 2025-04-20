package com.ile.syrin_x.ui.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.ile.syrin_x.R
import com.ile.syrin_x.data.enums.MusicCategory
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedPlaylist
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.spotify.SpotifyAlbum
import com.ile.syrin_x.data.model.spotify.SpotifyAlbumByIdResponse
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.icon.PlayIcon
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.viewModel.AlbumDetailsViewModel
import com.ile.syrin_x.viewModel.PlayerViewModel
import com.ile.syrin_x.viewModel.PlaylistDetailsViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import androidx.compose.material3.IconButton

@Composable
fun AlbumDetailsScreen(
    playerViewModel: PlayerViewModel,
    navHostController: NavHostController,
    albumId: String,
    musicSource: MusicSource,
    albumDetailsViewModel: AlbumDetailsViewModel = hiltViewModel()
) {

    LaunchedEffect(albumId, musicSource) {
        albumDetailsViewModel.getAlbumDetails(albumId, musicSource)
    }

    val playlistDetailsState = albumDetailsViewModel.searchFlow.collectAsState(initial = Response.Loading)
    val scope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = hostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Image(
            painter = painterResource(id = R.drawable.background_image_1),
            contentDescription = "Background image",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        when (val state = playlistDetailsState.value) {
            is Response.Loading -> {
                LoadingState()
            }
            is Response.Error -> {
                ErrorState(errorMessage = state.message)
            }
            is Response.Success -> {
                AlbumDetailsContent(
                    paddingValues = paddingValues,
                    searchFlowState = albumDetailsViewModel.searchFlow,
                    searchSuccess = {},
                    searchError = { errorMessage ->
                        scope.launch {
                            hostState.showSnackbar(errorMessage)
                        }
                    },
                    albumDetails = albumDetailsViewModel.albumDetails,
                    albumDetailsViewModel = albumDetailsViewModel,
                    playerViewModel = playerViewModel
                )
            }
        }
    }
}

@Composable
fun AlbumDetailsContent(
    paddingValues: PaddingValues,
    searchFlowState: MutableSharedFlow<Response<Any>>,
    searchSuccess: () -> Unit,
    searchError: (error: String) -> Unit,
    albumDetails: SpotifyAlbumByIdResponse?,
    albumDetailsViewModel: AlbumDetailsViewModel,
    playerViewModel: PlayerViewModel
) {
    val tracksLazyListState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        state = tracksLazyListState
    ) {
        albumDetails?.images?.firstOrNull()?.let { image ->
            item {
                AsyncImage(
                    model = image.url,
                    contentDescription = image.url,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(16.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = albumDetails?.name ?: "Unknown Album",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        albumDetails?.release_date?.let {
            item {
                Text(
                    text = "Release Date: $it",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                )
            }
        }

        albumDetails?.artists?.takeIf { it.isNotEmpty() }?.let { artists ->
            val artistNames = artists.joinToString(", ") { it.name ?: "Unknown Artist" }
            item {
                Text(
                    text = "Artists: $artistNames",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                )
            }
        }

        albumDetails?.album_type?.let {
            item {
                Text(
                    text = "Album Type: $it",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                )
            }
        }

        albumDetails?.total_tracks?.let {
            item {
                Text(
                    text = "Total Tracks: $it",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp)
                )
            }
        }

        item {
            IconButton(
                onClick = {
                    if (albumDetailsViewModel.tracksInAlbum.isNotEmpty()) {
                        playerViewModel.setTrackListAndPlayTracks(
                            albumDetailsViewModel.tracksInAlbum
                        )
                    }
                }
            ) {
                Icon(
                    imageVector = PlayIcon,
                    contentDescription = "Play Album",
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        item {
            Text(
                text = "Tracks",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            )
        }

        items(
            items = albumDetailsViewModel.tracksInAlbum,
            key = { track -> "${track.id}-${MusicCategory.ALBUMS}" }
        ) { track ->
            UnifiedAlbumTrackRow(track = track, playerViewModel = playerViewModel)
        }
    }

    SearchState(
        searchFlowState = searchFlowState,
        onSuccess = { searchSuccess() },
        onError = { error -> searchError(error) }
    )
}

@Composable
fun UnifiedAlbumTrackRow(
    track: UnifiedTrack,
    modifier: Modifier = Modifier,
    playerViewModel: PlayerViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                playerViewModel.playTrack(track)
            }
    ) {
        AsyncImage(
            model = track.artworkUrl,
            contentDescription = "${track.title} artwork",
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = track.title ?: "Unknown Title",
                maxLines = 1,
                style = MaterialTheme.typography.labelMedium
            )
            val subtitle = when {
                !track.albumName.isNullOrEmpty() -> track.albumName
                !track.artists.isNullOrEmpty() -> track.artists.joinToString(", ")
                else -> "Unknown Artist"
            }
            Text(
                text = subtitle,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        track.durationMs?.let { duration ->
            Text(
                text = formatDuration(duration),
                style = MaterialTheme.typography.displaySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AlbumDetailsState(
    playlistDetailsFlowState: MutableSharedFlow<Response<Any>>,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        playlistDetailsFlowState.collect {
            when(it) {
                is Response.Loading -> {
                    Log.i("Playlist Details State", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("Playlist Details State", it.message)
                    isLoading.value = false
                    onError(it.message)
                }

                is Response.Success -> {
                    Log.i("Playlist Details State", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}