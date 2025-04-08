package com.ile.syrin_x.ui.screen

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.ui.screen.common.MyCircularProgress
import com.ile.syrin_x.viewModel.TrackDetailsViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Composable
fun TrackDetailsScreen(
    navHostController: NavHostController,
    trackId: String,
    musicSource: MusicSource,
    trackDetailsViewModel: TrackDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(trackId, musicSource) {
        trackDetailsViewModel.getTrackDetails(trackId, musicSource)
    }

    val trackDetailsState = trackDetailsViewModel.searchFlow.collectAsState(initial = Response.Loading)

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

        when (val state = trackDetailsState.value) {
            is Response.Loading -> {
                LoadingState()
            }
            is Response.Error -> {
                ErrorState(errorMessage = state.message)
            }
            is Response.Success -> {
                Content(
                    paddingValues = paddingValues,
                    searchFlowState = trackDetailsViewModel.searchFlow,
                    searchSuccess = {},
                    searchError = { errorMessage ->
                        scope.launch {
                            hostState.showSnackbar(errorMessage)
                        }
                    },
                    trackDetails = trackDetailsViewModel.trackDetails
                )
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        MyCircularProgress()
    }
}

@Composable
fun ErrorState(errorMessage: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Error: $errorMessage", color = Color.Red)
    }
}

@Composable
fun Content(
    paddingValues: PaddingValues,
    searchFlowState: MutableSharedFlow<Response<Any>>,
    searchSuccess: () -> Unit,
    searchError: (error: String) -> Unit,
    trackDetails: UnifiedTrack?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        trackDetails?.artworkUrl?.let { artworkUrl ->
            AsyncImage(
                model = artworkUrl,
                contentDescription = artworkUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = trackDetails?.title ?: "Unknown Title",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        trackDetails?.albumName?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
            )
        }

        trackDetails?.artists?.let {
            Text(
                text = it.joinToString(", "),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
            )
        }

        trackDetails?.genre?.let {
            Text(
                text = "Genre: $it",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
            )
        }

        trackDetails?.durationMs?.let {
            Text(
                text = "Duration: ${formatDuration(it)}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
            )
        }

        Text(
            text = if (trackDetails?.explicit == true) "Explicit Content" else "Clean Content",
            style = MaterialTheme.typography.bodyMedium,
            color = if (trackDetails?.explicit == true) Color.Red else Color.Gray,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
        )

        trackDetails?.popularity?.let {
            Text(
                text = "Popularity: $it",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
            )
        }

        trackDetails?.playbackUrl?.let {
            Text(
                text = "Playback URL: $it",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
            )
        }

    }

    SearchState(
        searchFlowState = searchFlowState,
        onSuccess = { searchSuccess() },
        onError = { error -> searchError(error) }
    )
}

@Composable
fun TrackDetailsState(
    trackDetailsFlowState: MutableSharedFlow<Response<Any>>,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit
) {
    val isLoading = remember { mutableStateOf(false) }
    if (isLoading.value) MyCircularProgress()
    LaunchedEffect(Unit) {
        trackDetailsFlowState.collect {
            when(it) {
                is Response.Loading -> {
                    Log.i("Track Details State", "Loading")
                    isLoading.value = true
                }

                is Response.Error -> {
                    Log.e("Track Details State", it.message)
                    isLoading.value = false
                    onError(it.message)
                }

                is Response.Success -> {
                    Log.i("Track Details State", "Success")
                    isLoading.value = false
                    onSuccess()
                }
            }
        }
    }
}