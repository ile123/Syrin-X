package com.ile.syrin_x.ui.screen

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.ile.syrin_x.R
import com.ile.syrin_x.data.enums.MusicCategory
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedPlaylist
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.UnifiedUser
import com.ile.syrin_x.data.model.spotify.SpotifyAlbum
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.viewModel.SearchViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun SearchResultScreen(
    navHostController: NavHostController,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }

    fun searchByMusicCategory(musicCategory: MusicCategory) {
        when(musicCategory) {
            MusicCategory.TRACKS -> searchViewModel.fetchMoreTracksForInfiniteScroll()
            MusicCategory.PLAYLISTS -> searchViewModel.fetchMorePlaylistsForInfiniteScroll()
            MusicCategory.ALBUMS -> searchViewModel.fetchMoreAlbumsForInfiniteScroll()
            MusicCategory.USERS -> searchViewModel.fetchMoreUsersForInfiniteScroll()
        }
    }

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

        Content(
            paddingValues = paddingValues,
            searchFlowState = searchViewModel.searchFlow,
            onEndOfListHit = { musicCategory -> searchByMusicCategory(musicCategory) },
            searchSuccess = {  },
            searchError = { errorMessage ->
                scope.launch {
                    hostState.showSnackbar(errorMessage)
                }
            },
            searchViewModel = searchViewModel,
            navHostController = navHostController
        )
    }
}

@Composable
fun Content(
    paddingValues: PaddingValues,
    searchFlowState: MutableSharedFlow<Response<Any>>,
    onEndOfListHit: (MusicCategory) -> Unit,
    searchSuccess: () -> Unit,
    searchError: (error: String) -> Unit,
    searchViewModel: SearchViewModel,
    navHostController: NavHostController
) {
    val tracksLazyListState = rememberLazyListState()
    val playlistsLazyListState = rememberLazyListState()
    val albumsLazyListState = rememberLazyListState()
    val usersLazyListState = rememberLazyListState()

    LaunchedEffect(tracksLazyListState) {
        snapshotFlow { tracksLazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == searchViewModel.searchedTracks.size - 1) {
                    onEndOfListHit(MusicCategory.TRACKS)
                }
            }
    }

    LaunchedEffect(playlistsLazyListState) {
        snapshotFlow { playlistsLazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == searchViewModel.searchedPlaylists.size - 1) {
                    onEndOfListHit(MusicCategory.PLAYLISTS)
                }
            }
    }

    LaunchedEffect(albumsLazyListState) {
        snapshotFlow { albumsLazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == searchViewModel.searchedAlbums.size - 1) {
                    onEndOfListHit(MusicCategory.ALBUMS)
                }
            }
    }

    LaunchedEffect(usersLazyListState) {
        snapshotFlow { usersLazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == searchViewModel.searchedUsers.size - 1) {
                    onEndOfListHit(MusicCategory.USERS)
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        var selectedCategory by remember { mutableStateOf(MusicCategory.TRACKS) }

        MusicCategoryTabs(
            musicSource = searchViewModel.searchedMusicSource,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedCategory) {
            MusicCategory.TRACKS -> {
                LazyColumn(state = tracksLazyListState) {
                    items(searchViewModel.searchedTracks, key = { item -> "${item.id}-${MusicCategory.TRACKS}" }) { track ->
                        UnifiedTrackRow(track = track, searchViewModel.searchedMusicSource, navHostController)
                    }
                }
            }
            MusicCategory.PLAYLISTS -> {
                LazyColumn(state = playlistsLazyListState) {
                    items(searchViewModel.searchedPlaylists, key = { item -> "${item.id}-${MusicCategory.PLAYLISTS}" }) { playlist ->
                        UnifiedPlaylistRow(playlist = playlist, searchViewModel.searchedMusicSource, navHostController)
                    }
                }
            }
            MusicCategory.ALBUMS -> {
                if (searchViewModel.searchedMusicSource == MusicSource.SPOTIFY) {
                    LazyColumn(state = albumsLazyListState) {
                        items(searchViewModel.searchedAlbums, key = { item -> "${item.id}-${MusicCategory.ALBUMS}" }) { album ->
                            UnifiedAlbumRow(album = album, searchViewModel.searchedMusicSource, navHostController)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Albums are not available for SoundCloud",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            MusicCategory.USERS -> {
                LazyColumn(state = usersLazyListState) {
                    items(searchViewModel.searchedUsers, key = { item -> "${item.id}-${MusicCategory.USERS}" }) { user ->
                        UnifiedUserRow(user = user, searchViewModel.searchedMusicSource, navHostController)
                    }
                }
            }
        }
    }

    SearchResultState(
        searchFlowState = searchFlowState,
        onSuccess = { searchSuccess() },
        onError = { error -> searchError(error) }
    )
}


@Composable
fun SearchResultState(
    searchFlowState: MutableSharedFlow<Response<Any>>,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        searchFlowState.collect {
            when (it) {
                is Response.Loading -> {
                    Log.i("Search state", "Loading")
                    isLoading.value = true
                }
                is Response.Error -> {
                    Log.e("Search state", it.message)
                    isLoading.value = false
                    onError(it.message)
                }
                is Response.Success -> {
                    Log.i("Search state", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}

fun formatDuration(durationMs: Int): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

@Composable
fun UnifiedTrackRow(
    track: UnifiedTrack,
    musicSource: MusicSource,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { navHostController.navigate("track_details_screen/${track.id}/${musicSource.name}") })
            .padding(8.dp)
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
fun UnifiedAlbumRow(
    album: SpotifyAlbum,
    musicSource: MusicSource,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { navHostController.navigate("album_details_screen/${album.id}/${musicSource.name}") })
            .padding(8.dp)
    ) {
        val imageUrl = album.images?.firstOrNull()?.url
        AsyncImage(
            model = imageUrl,
            contentDescription = "${album.name} album artwork",
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = album.name ?: "Unknown Album",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = album.artists?.get(0)?.name ?: "Unknown Artist",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Magenta
            )
        }
    }
}

@Composable
fun UnifiedPlaylistRow(
    playlist: UnifiedPlaylist,
    musicSource: MusicSource,
    navHostController: NavHostController,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { navHostController.navigate("playlist_details_screen/${playlist.id}/${musicSource.name}") })
            .padding(8.dp)
    ) {
        val imageUrl = playlist.images?.firstOrNull()?.url
        AsyncImage(
            model = imageUrl,
            contentDescription = "${playlist.name} playlist image",
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = playlist.name ?: "Unknown Playlist",
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium
            )
            if (!playlist.description.isNullOrEmpty()) {
                Text(
                    text = playlist.description,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun UnifiedUserRow(
    user: UnifiedUser,
    musicSource: MusicSource,
    navHostController: NavHostController,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "${user.name} avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name ?: "Unknown User",
                style = MaterialTheme.typography.labelSmall
            )
            user.type?.let { type ->
                Text(
                    text = type.replaceFirstChar { char -> char.uppercase(Locale.getDefault()) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun MusicCategoryTabs(
    musicSource: MusicSource,
    selectedCategory: MusicCategory,
    onCategorySelected: (MusicCategory) -> Unit
) {
    val categories = when (musicSource) {
        MusicSource.SPOTIFY -> listOfNotNull(
            MusicCategory.TRACKS,
            MusicCategory.PLAYLISTS,
            MusicCategory.ALBUMS,
            MusicCategory.USERS
        )
        MusicSource.SOUNDCLOUD -> listOfNotNull(
            MusicCategory.TRACKS,
            MusicCategory.PLAYLISTS,
            MusicCategory.USERS
        )
        MusicSource.NOT_SPECIFIED -> emptyList()
    }

    if (categories.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No categories available", style = MaterialTheme.typography.bodyMedium)
        }
    } else {
        val selectedIndex = categories.indexOf(selectedCategory).let { if (it >= 0) it else 0 }

        TabRow(
            selectedTabIndex = selectedIndex,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            indicator = { tabPositions ->
                TabRowDefaults
                    .Indicator(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedIndex])
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        ) {
            categories.forEachIndexed { index, category ->
                Tab(
                    selected = category == selectedCategory,
                    onClick = { onCategorySelected(category) },
                    text = {
                        Text(
                            text = category.displayName,
                            style = if (category == selectedCategory) {
                                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            } else {
                                MaterialTheme.typography.bodyMedium
                            },
                            color = if (category == selectedCategory) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            }
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .animateContentSize()
                )
            }
        }
    }
}

