package com.ile.syrin_x.domain.player

import com.ile.syrin_x.data.enums.MusicPlayerRepeatMode
import com.ile.syrin_x.data.model.UnifiedTrack
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerController {
    fun play(track: UnifiedTrack)
    fun pause()
    fun resume()
    fun seekTo(positionMs: Long)
    fun skipToNext()
    fun skipToPrevious()
    fun setCurrentRepeatMode(repeatMode: MusicPlayerRepeatMode)
    fun release()

    val isPlaying: StateFlow<Boolean>
    val playbackPosition: StateFlow<Long>
    val duration: StateFlow<Long>
}
