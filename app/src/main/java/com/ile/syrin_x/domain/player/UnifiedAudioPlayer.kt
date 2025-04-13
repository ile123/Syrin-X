package com.ile.syrin_x.domain.player
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.enums.MusicPlayerRepeatMode
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.service.MusicPlaybackService
import com.ile.syrin_x.utils.GlobalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UnifiedAudioPlayer(
    private val context: Context,
    private val spotifyRemoteClient: SpotifyRemoteClient
) : AudioPlayerController {

    private val exoPlayer = ExoPlayer.Builder(context).build()
    private var currentTrack: UnifiedTrack? = null
    private var currentSource: MusicSource? = null

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _playbackPosition = MutableStateFlow(0L)
    override val playbackPosition: StateFlow<Long> = _playbackPosition

    private val _duration = MutableStateFlow(0L)
    override val duration: StateFlow<Long> = _duration

    private var _currentRepeatMode: MusicPlayerRepeatMode = MusicPlayerRepeatMode.OFF
    val currentRepeatMode: MusicPlayerRepeatMode
        get() = _currentRepeatMode

    private fun startServiceIfNeeded() {
        val intent = Intent(context, MusicPlaybackService::class.java)
        ContextCompat.startForegroundService(context, intent)
    }

    override fun play(track: UnifiedTrack) {
        currentTrack = track
        currentSource = track.musicSource

        startServiceIfNeeded()

        when (track.musicSource) {
            MusicSource.SOUNDCLOUD -> {
                Log.d("UnifiedAudioPlayer", "Playing song from SoundCloud.")
                playWithExoPlayer(track)
            }

            MusicSource.SPOTIFY -> {
                Log.d("UnifiedAudioPlayer", "Attempting to play song from Spotify.")

                CoroutineScope(Dispatchers.Main).launch {
                    val result = spotifyRemoteClient.connect()
                    result.fold(
                        onSuccess = {
                            spotifyRemoteClient.play(track)
                        },
                        onFailure = {
                            Log.e("UnifiedAudioPlayer", "Spotify connect failed", it)
                        }
                    )
                }

            }

            MusicSource.NOT_SPECIFIED -> {
                Log.w("UnifiedAudioPlayer", "Music source not specified for track: ${track.title}")
            }
        }
    }

    private fun playWithExoPlayer(track: UnifiedTrack) {
        val playbackUrl = track.playbackUrl ?: return

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("SyrinX-App/1.0")
            .setDefaultRequestProperties(
                mapOf("Authorization" to "OAuth ${GlobalContext.Tokens.soundCloudToken}")
            )

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(playbackUrl))

        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d("ExoPlayer", "isPlaying: $isPlaying")
                _isPlaying.value = isPlaying
            }

            override fun onPlaybackStateChanged(state: Int) {
                Log.d("ExoPlayer", "Playback state: $state")
                if (state == Player.STATE_READY) {
                    _duration.value = exoPlayer.duration
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e("ExoPlayer", "Playback error: ${error.message}", error)
            }
        })

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    override fun pause() {
        when (currentSource) {
            MusicSource.SOUNDCLOUD -> exoPlayer.pause()
            MusicSource.SPOTIFY -> spotifyRemoteClient.pause()
            else -> {}
        }
    }

    override fun resume() {
        when (currentSource) {
            MusicSource.SOUNDCLOUD -> exoPlayer.play()
            MusicSource.SPOTIFY -> spotifyRemoteClient.resume()
            else -> {}
        }
    }

    override fun seekTo(positionMs: Long) {
        when (currentSource) {
            MusicSource.SOUNDCLOUD -> exoPlayer.seekTo(positionMs)
            MusicSource.SPOTIFY -> spotifyRemoteClient.seekTo(positionMs)
            else -> {}
        }
    }

    override fun skipToNext() {
        spotifyRemoteClient.skipToNext()
    }

    override fun skipToPrevious() {
        spotifyRemoteClient.skipToPrevious()
    }

    override fun setCurrentRepeatMode(repeatMode: MusicPlayerRepeatMode) {
        _currentRepeatMode = repeatMode
        spotifyRemoteClient.setRepeatMode(repeatMode)
    }

    override fun release() {
        exoPlayer.release()
        spotifyRemoteClient.disconnect()
    }
}

