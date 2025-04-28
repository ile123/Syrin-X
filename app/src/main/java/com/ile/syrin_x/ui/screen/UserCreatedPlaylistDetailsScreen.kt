package com.ile.syrin_x.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ile.syrin_x.data.enums.UserCreatedPlaylistTrackAction
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.usercreatedplaylist.UserCreatedPlaylist
import com.ile.syrin_x.data.model.usercreatedplaylist.toUnifiedTrack
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.icon.PlayIcon
import com.ile.syrin_x.viewModel.PlayerViewModel
import com.ile.syrin_x.viewModel.PlaylistManagementViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
            TopAppBar(title = { Text("Playlist Details") })
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
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = {
                            val unified = tracks.map { it.toUnifiedTrack() }
                            playerViewModel.setTrackListAndPlayTracks(unified)
                        }) {
                            Icon(
                                imageVector   = PlayIcon,
                                contentDescription = "Play Playlist",
                                modifier      = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                items(
                    items = tracks,
                    key = { it.userCreatedPlaylistTrackId }
                ) { track ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navHostController.navigate("track_details_screen/${track.trackId}/${track.musicSource}")
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        IconButton(onClick = {
                            playerViewModel.playTrack(track.toUnifiedTrack())
                        }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${track.title.orEmpty()} – ${track.artists?.firstOrNull().orEmpty()}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = {
                            playlistManagementViewModel.addOrRemoveTrackFromPlaylist(
                                track      = track.toUnifiedTrack(),
                                playlistId = playlist!!.userCreatedPlaylistId,
                                action     = UserCreatedPlaylistTrackAction.REMOVE_TRACK
                            )
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove")
                        }
                    }
                }
            }
        } else {
            Text(
                "No songs have been added to this playlist.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center),
                style = MaterialTheme.typography.bodyMedium
            )
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