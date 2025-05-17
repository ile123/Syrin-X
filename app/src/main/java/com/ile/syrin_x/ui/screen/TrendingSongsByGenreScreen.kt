package com.ile.syrin_x.ui.screen

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
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
import com.ile.syrin_x.data.enums.MusicCategory
import com.ile.syrin_x.data.model.deezer.TrackByGenre
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.screen.common.BottomBarNavigationComponent
import com.ile.syrin_x.ui.screen.common.HeaderComponent
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.utils.formatDuration
import com.ile.syrin_x.viewModel.TrendingSongsByGenreViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@Composable
fun TrendingSongsByGenreScreen(
    navHostController: NavHostController,
    genreId: String,
    trendingSongsByGenreViewModel: TrendingSongsByGenreViewModel = hiltViewModel()
) {

    val scope = rememberCoroutineScope()
    val hostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        trendingSongsByGenreViewModel.fetchTrendingSongsByGenre(genreId.toLong())
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = hostState) },
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HeaderComponent(navHostController)
        },
        bottomBar = {
            BottomBarNavigationComponent(navHostController)
        }
    ) { paddingValues ->
        TrendingSongsByGenreContent(
            paddingValues,
            trendingSongsByGenreViewModel.trackByGenreFlow,
            onEndOfListHit = {
                trendingSongsByGenreViewModel.fetchMoreTrendingSongsByGenreForInfiniteScroll(genreId.toLong())
            },
            searchSongByGenreSuccess = { },
            searchSongByGenreError = { errorMessage ->
                scope.launch {
                    hostState.showSnackbar(errorMessage)
                }
            },
            trendingSongsByGenreViewModel
        )
    }

}

@Composable
fun TrendingSongsByGenreContent(
    paddingValues: PaddingValues,
    tracksByGenreFlowState: MutableSharedFlow<Response<Any>>,
    onEndOfListHit: () -> Unit,
    searchSongByGenreSuccess: () -> Unit,
    searchSongByGenreError: (error: String) -> Unit,
    trendingSongsByGenreViewModel: TrendingSongsByGenreViewModel
) {
    val chartingTracksLazyListState = rememberLazyListState()

    LaunchedEffect(chartingTracksLazyListState) {
        snapshotFlow { chartingTracksLazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                if (lastVisibleItemIndex == trendingSongsByGenreViewModel.tracks.size - 1) {
                    onEndOfListHit()
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Text(
            "Current topping songs",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(state = chartingTracksLazyListState) {
            items(trendingSongsByGenreViewModel.tracks, key = { item -> item.id }) { track ->
                TrendingSongsByGenreRow(track)
            }
        }
    }

    SearchSongsByGenreState(tracksByGenreFlowState, onSuccess = { }, onError = { })
}

@Composable
fun TrendingSongsByGenreRow(
    track: TrackByGenre,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AsyncImage(
                model = track.album.cover_medium ?: track.album.cover,
                contentDescription = "Album cover for ${track.title}",
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.music_note_icon),
                error = painterResource(R.drawable.music_expandall_icon),
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = track.artist.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatDuration(track.duration * 1000),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SearchSongsByGenreState(
    tracksByGenreFlowState: MutableSharedFlow<Response<Any>>,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        tracksByGenreFlowState.collect {
            when (it) {
                is Response.Loading -> {
                    Log.i("TracksByGenreFlow state", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("TracksByGenreFlow state", it.message)
                    isLoading.value = false
                    onError(it.message)
                }

                is Response.Success -> {
                    Log.i("TracksByGenreFlow state", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}