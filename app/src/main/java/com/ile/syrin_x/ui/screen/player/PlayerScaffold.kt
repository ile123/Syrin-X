package com.ile.syrin_x.ui.screen.player

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()
    val shuffleMode by viewModel.shuffleMode.collectAsStateWithLifecycle()

    var showFullPlayer by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            if (event is PlayerViewModel.PlayerUiEvent.ExpandPlayer) {
                showFullPlayer = true
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        if (currentTrack != null && !showFullPlayer) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(
                        WindowInsets.navigationBars
                            .only(WindowInsetsSides.Bottom)
                    )
                    .padding(bottom = 80.dp)
                    .zIndex(1f)
            ) {
                MiniPlayerBar(
                    track = currentTrack!!,
                    isPlaying = isPlaying,
                    onPlayPauseClicked = { viewModel.togglePlayPause() },
                    onPreviousClicked = { viewModel.skipPrevious() },
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
                shuffleMode = shuffleMode,
                repeatMode = repeatMode,
                onBack = { showFullPlayer = false },
                onPlayPauseToggle = { viewModel.togglePlayPause() },
                onSeekTo = { viewModel.seekTo(it) },
                onNext = { viewModel.skipNext() },
                onPrevious = { viewModel.skipPrevious() },
                onShuffleToggle = { viewModel.toggleShuffleMode() },
                onRepeatToggle = { viewModel.toggleCurrentRepeatMode() }
            )
        }
    }
}
