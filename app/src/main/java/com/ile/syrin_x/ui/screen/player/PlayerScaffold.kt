package com.ile.syrin_x.ui.screen.player

import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ile.syrin_x.data.enums.MusicPlayerRepeatMode
import com.ile.syrin_x.viewModel.PlayerViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScaffold(
    viewModel: PlayerViewModel = hiltViewModel(),
    content: @Composable () -> Unit
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val position by viewModel.playbackPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is PlayerViewModel.PlayerUiEvent.ExpandPlayer -> {
                    scaffoldState.bottomSheetState.expand()
                }
            }
        }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = if (currentTrack == null) 0.dp else 64.dp,
        sheetContent = {
            if (currentTrack != null) {
                FullPlayerScreen(
                    track = currentTrack!!,
                    isPlaying = isPlaying,
                    playbackPosition = position,
                    duration = duration,
                    repeatMode = MusicPlayerRepeatMode.OFF,
                    onBack = {
                        coroutineScope.launch {
                            scaffoldState.bottomSheetState.partialExpand()
                        }
                    },
                    onPlayPauseToggle = { viewModel.togglePlayPause() },
                    onSeekTo = { viewModel.seekTo(it) },
                    onNext = { viewModel.skipNext() },
                    onPrevious = { viewModel.skipPrevious() },
                    onRepeatToggle = { viewModel.toggleCurrentRepeatMode() }
                )
            }
        }
    ) {
        content()

        if (currentTrack != null) {
            MiniPlayerBar(
                track = currentTrack!!,
                isPlaying = isPlaying,
                onPlayPauseClicked = { viewModel.togglePlayPause() },
                onNextClicked = { viewModel.skipNext() },
                onBarClicked = {
                    coroutineScope.launch {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            )
        }
    }
}
