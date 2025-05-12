package com.ile.syrin_x.viewModel

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.enums.MusicPlayerRepeatMode
import com.ile.syrin_x.data.enums.ShuffleMode
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.domain.player.UnifiedAudioPlayer
import com.ile.syrin_x.domain.repository.PreviouslyPlayedTrackRepository
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val audioPlayer: UnifiedAudioPlayer,
    val previouslyPlayedTrackRepository: PreviouslyPlayedTrackRepository,
    val getUserUidUseCase: GetUserUidUseCase,
) : ViewModel() {

    val isPlaying: StateFlow<Boolean> = audioPlayer.isPlaying
    val playbackPosition: StateFlow<Long> = audioPlayer.playbackPosition
    val duration: StateFlow<Long> = audioPlayer.duration
    val currentTrack = MutableStateFlow<UnifiedTrack?>(null)

    private val trackList = mutableListOf<UnifiedTrack>()
    private var currentIndex = 0

    private val _uiEvent = MutableSharedFlow<PlayerUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    sealed class PlayerUiEvent {
        data object ExpandPlayer : PlayerUiEvent()
    }

    init {
        audioPlayer.onSkipNext = { skipNext() }
        audioPlayer.onSkipPrevious = { skipPrevious() }
    }


    fun setTrackListAndPlayTracks(tracks: List<UnifiedTrack>) {
        trackList.clear()
        trackList.addAll(tracks)
        currentIndex = 0
        playTrack(trackList[currentIndex])
    }

    fun playTrack(track: UnifiedTrack) {
        if (currentTrack.value != null) {
            audioPlayer.stop()
        }
        currentTrack.value = track
        audioPlayer.play(track)
        viewModelScope.launch {
            _uiEvent.emit(PlayerUiEvent.ExpandPlayer)
            getUserUidUseCase.invoke().collect { userUuid ->
                previouslyPlayedTrackRepository.addPreviouslyPlayedTrack(track, userUuid)
            }
        }
    }

    fun togglePlayPause() {
        if (isPlaying.value) audioPlayer.pause() else audioPlayer.resume()
    }

    fun seekTo(positionMs: Long) = audioPlayer.seekTo(positionMs)

    fun skipNext() {
        if (currentIndex < trackList.size - 1) {
            currentIndex++
            playTrack(trackList[currentIndex])
        }
    }

    fun skipPrevious() {
        if (currentIndex > 0) {
            currentIndex--
            playTrack(trackList[currentIndex])
        }
    }

    fun toggleCurrentRepeatMode() {
        val next = when (audioPlayer.currentRepeatMode) {
            MusicPlayerRepeatMode.OFF -> MusicPlayerRepeatMode.ALL
            MusicPlayerRepeatMode.ALL -> MusicPlayerRepeatMode.OFF
        }
        audioPlayer.setCurrentRepeatMode(next)
    }

    fun toggleCurrentShuffleMode() {
        val next = when (audioPlayer.currentShuffleMode) {
            ShuffleMode.OFF -> ShuffleMode.ON
            ShuffleMode.ON -> ShuffleMode.OFF
        }
        // Add call for UnifiedAudioPlayer here
    }

    override fun onCleared() {
        audioPlayer.release()
        super.onCleared()
    }
}

