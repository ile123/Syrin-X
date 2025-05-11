package com.ile.syrin_x.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.ile.syrin_x.R
import com.ile.syrin_x.data.enums.UserCreatedPlaylistTrackAction
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.usercreatedplaylist.UserCreatedPlaylist
import com.ile.syrin_x.data.model.usercreatedplaylist.UserCreatedPlaylistTrack
import com.ile.syrin_x.data.model.usercreatedplaylist.toUnifiedTrack
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.icon.PlayIcon
import com.ile.syrin_x.ui.screen.common.BottomBarNavigationComponent
import com.ile.syrin_x.ui.screen.common.HeaderComponent
import com.ile.syrin_x.viewModel.PlayerViewModel
import com.ile.syrin_x.viewModel.PlaylistManagementViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Composable
fun UserCreatedPlaylistDetailsScreen(
    playerViewModel: PlayerViewModel,
    navHostController: NavHostController,
    userCreatedPlaylistId: String?,
    playlistManagementViewModel: PlaylistManagementViewModel = hiltViewModel(),
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            HeaderComponent(navHostController)
        },
        bottomBar = {
            BottomBarNavigationComponent(navHostController)
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Content(
            hostState = snackbarHostState,
            dataFlow = playlistManagementViewModel.dataFlow,
            userCreatedPlaylistId = userCreatedPlaylistId,
            playlistManagementViewModel = playlistManagementViewModel,
            playerViewModel = playerViewModel,
            navHostController = navHostController,
            paddingValues = paddingValues
        )
    }
}

@Composable
private fun Content(
    hostState: SnackbarHostState,
    dataFlow: MutableSharedFlow<Response<Any>>,
    userCreatedPlaylistId: String?,
    playlistManagementViewModel: PlaylistManagementViewModel,
    playerViewModel: PlayerViewModel,
    navHostController: NavHostController,
    paddingValues: PaddingValues
) {
    val scope = rememberCoroutineScope()
    var playlist by remember { mutableStateOf<UserCreatedPlaylist?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(userCreatedPlaylistId) {
        userCreatedPlaylistId?.let {
            playlistManagementViewModel.getUserPlaylistById(it)
        }
    }

    PlaylistDetailsState(
        flow = dataFlow,
        onLoading = { isLoading = true },
        onError = { msg ->
            isLoading = false
            scope.launch { hostState.showSnackbar(msg) }
        },
        onSuccess = { pl ->
            isLoading = false
            playlist = pl
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        val tracks = playlist?.tracks.orEmpty()

        if (tracks.isNotEmpty()) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Songs In Playlist",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            style = MaterialTheme.typography.displaySmall,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Normal
                        )
                        IconButton(onClick = {
                            val unified = tracks.map { it.toUnifiedTrack() }
                            playerViewModel.setTrackListAndPlayTracks(unified)
                        }) {
                            Icon(
                                imageVector = PlayIcon,
                                contentDescription = "Play Playlist",
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }
                }

                items(
                    items = tracks,
                    key = { it.userCreatedPlaylistTrackId }
                ) { track ->
                    PlaylistTrackRow(
                        track = track,
                        onClick = {
                            navHostController.navigate("track_details_screen/${track.trackId}/${track.musicSource}")
                        },
                        onPlay = {
                            playerViewModel.playTrack(track.toUnifiedTrack())
                        },
                        onRemove = {
                            playlistManagementViewModel.addOrRemoveTrackFromPlaylist(
                                track = track.toUnifiedTrack(),
                                playlistId = playlist!!.userCreatedPlaylistId,
                                action = UserCreatedPlaylistTrackAction.REMOVE_TRACK
                            )
                        }
                    )
                }
            }
        } else {
            Text(
                "No songs have been added to this playlist.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
private fun PlaylistTrackRow(
    track: UserCreatedPlaylistTrack,
    onClick: () -> Unit,
    onPlay: () -> Unit,
    onRemove: () -> Unit,
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
            AsyncImage(
                model = track.artworkUrl,
                contentDescription = track.title,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.music_note_icon),
                error = painterResource(R.drawable.music_note_icon),
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = onPlay) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${track.title.orEmpty()} – ${track.artists?.firstOrNull().orEmpty()}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove from playlist",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun PlaylistDetailsState(
    flow: MutableSharedFlow<Response<Any>>,
    onLoading: () -> Unit,
    onSuccess: (UserCreatedPlaylist) -> Unit,
    onError: (String) -> Unit
) {
    LaunchedEffect(flow) {
        flow.collect { resp ->
            when (resp) {
                is Response.Loading -> {
                    Log.i("PlaylistDetailsState", "Loading")
                    onLoading()
                }

                is Response.Error -> {
                    Log.e("PlaylistDetailsState", resp.message)
                    onError(resp.message)
                }

                is Response.Success<*> -> {
                    val pl = resp.data as UserCreatedPlaylist
                    Log.i("PlaylistDetailsState", "Success")
                    onSuccess(pl)
                }
            }
        }
    }
}