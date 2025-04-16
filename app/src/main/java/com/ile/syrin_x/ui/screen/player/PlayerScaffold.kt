package com.ile.syrin_x.ui.screen.player

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ile.syrin_x.viewModel.PlayerViewModel

@Composable
fun PlayerScaffold(
    viewModel: PlayerViewModel,
    content: @Composable () -> Unit
) {
    val currentTrack by viewModel.currentTrack.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val position by viewModel.playbackPosition.collectAsState()
    val repeatMode = viewModel.audioPlayer.currentRepeatMode

    var showFullPlayer by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is PlayerViewModel.PlayerUiEvent.ExpandPlayer) {
                showFullPlayer = true
            }
        }
    }

    if (showFullPlayer) {
        BackHandler {
            showFullPlayer = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.weight(1f)) {
                content()
            }

            if (currentTrack != null && !showFullPlayer) {
                MiniPlayerBar(
                    track = currentTrack!!,
                    isPlaying = isPlaying,
                    onPlayPauseClicked = { viewModel.togglePlayPause() },
                    onNextClicked = { viewModel.skipNext() },
                    onBarClicked = { showFullPlayer = true }
                )
            }
        }

        if (currentTrack != null && showFullPlayer) {
            FullPlayerScreen(
                track = currentTrack!!,
                isPlaying = isPlaying,
                playbackPosition = position,
                duration = duration,
                repeatMode = repeatMode,
                onBack = { showFullPlayer = false },
                onPlayPauseToggle = { viewModel.togglePlayPause() },
                onSeekTo = { viewModel.seekTo(it) },
                onNext = { viewModel.skipNext() },
                onPrevious = { viewModel.skipPrevious() },
                onRepeatToggle = { viewModel.toggleCurrentRepeatMode() }
            )
        }
    }
}
