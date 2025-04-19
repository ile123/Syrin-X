package com.ile.syrin_x.domain.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.ile.syrin_x.MainActivity
import com.ile.syrin_x.data.enums.MusicPlayerRepeatMode
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.service.MusicPlaybackService
import com.ile.syrin_x.utils.GlobalContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton
import coil.ImageLoader
import coil.request.ImageRequest
import com.ile.syrin_x.domain.repository.SpotifyRepository
import androidx.core.net.toUri

@Singleton
class UnifiedAudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spotifyRepository: SpotifyRepository
) : AudioPlayerController {

    private var exoPlayer = ExoPlayer.Builder(context).build()
    private var isReleased = false

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

    lateinit var notificationManager: PlayerNotificationManager
    lateinit var currentNotification: Notification
    private lateinit var mediaSession: MediaSessionCompat

    private var pollingScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var positionPollingJob: Job? = null

    init {
        setupMediaSession()
        setupNotification()
    }

    private fun ensurePlayerInitialized() {
        if (isReleased) {
            exoPlayer = ExoPlayer.Builder(context).build()
            exoPlayer.addListener(playerListener)
            notificationManager.setPlayer(exoPlayer)
            isReleased = false
        }
    }

    private fun setupMediaSession() {
        mediaSession = MediaSessionCompat(context, "SyrinXMediaSession").apply {
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() = resume()
                override fun onPause() = pause()
                override fun onSkipToNext() = skipToNext()
                override fun onSkipToPrevious() = skipToPrevious()
                override fun onSeekTo(pos: Long) = seekTo(pos)
            })
            isActive = true
        }
    }

    private fun setupNotification() {
        val channelId = "music_playback_channel"
        val channel = NotificationChannel(channelId, "Playback", NotificationManager.IMPORTANCE_LOW)
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        notificationManager = PlayerNotificationManager.Builder(context, 1001, channelId)
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return player.mediaMetadata.title ?: "Unknown"
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val intent = Intent(context, MainActivity::class.java).apply {
                        action = Intent.ACTION_MAIN
                        addCategory(Intent.CATEGORY_LAUNCHER)
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return player.mediaMetadata.artist
                }

                override fun getCurrentLargeIcon(player: Player, callback: PlayerNotificationManager.BitmapCallback): Bitmap? {
                    val artworkUri = player.mediaMetadata.artworkUri ?: return null
                    val imageLoader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(artworkUri)
                        .allowHardware(false)
                        .target { result ->
                            if (result is BitmapDrawable) {
                                callback.onBitmap(result.bitmap)
                            }
                        }
                        .build()
                    imageLoader.enqueue(request)
                    return null
                }
            })
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(id: Int, notification: Notification, ongoing: Boolean) {
                    currentNotification = notification
                    val serviceIntent = Intent(context, MusicPlaybackService::class.java)
                    serviceIntent.putExtra("notification_id", id)
                    ContextCompat.startForegroundService(context, serviceIntent)
                }

                override fun onNotificationCancelled(id: Int, dismissedByUser: Boolean) {
                    if (exoPlayer.isPlaying) pause()
                }
            })
            .build()

        notificationManager.setPlayer(exoPlayer)
        notificationManager.setMediaSessionToken(mediaSession.sessionToken)
        notificationManager.setUsePlayPauseActions(true)
        notificationManager.setUseNextAction(true)
        notificationManager.setUsePreviousAction(true)
        notificationManager.setUseChronometer(true)
    }

    override fun play(track: UnifiedTrack) {
        ensurePlayerInitialized()
        currentTrack = track
        currentSource = track.musicSource

        when (track.musicSource) {
            MusicSource.SOUNDCLOUD -> playWithExoPlayer(track)
            MusicSource.SPOTIFY -> CoroutineScope(Dispatchers.Main).launch {
                val token = GlobalContext.Tokens.spotifyToken
                spotifyRepository.play(track, token)
                startPositionPolling()
            }
            else -> {}
        }
    }

    private fun playWithExoPlayer(track: UnifiedTrack) {
        val playbackUrl = track.playbackUrl ?: return

        val mediaItem = MediaItem.Builder()
            .setUri(playbackUrl)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(track.title)
                    .setArtist(track.artists?.joinToString(", "))
                    .setArtworkUri(track.artworkUrl?.toUri())
                    .build()
            )
            .build()

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("SyrinX-App/1.0")
            .setDefaultRequestProperties(mapOf("Authorization" to "OAuth ${GlobalContext.Tokens.soundCloudToken}"))

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)

        exoPlayer.clearMediaItems()
        exoPlayer.removeListener(playerListener)
        exoPlayer.addListener(playerListener)

        exoPlayer.seekTo(0)
        _playbackPosition.value = 0L

        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
        exoPlayer.play()

        startPositionPolling()
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            updateMediaSessionPlaybackState()
            startPositionPolling()
        }

        override fun onPlaybackStateChanged(state: Int) {
            if (state == Player.STATE_READY) {
                _duration.value = exoPlayer.duration
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e("UnifiedAudioPlayer", "Playback error", error)
        }
    }

    private fun startPositionPolling() {
        positionPollingJob?.cancel()
        positionPollingJob = pollingScope.launch {
            while (isActive) {
                if (currentSource == MusicSource.SOUNDCLOUD) {
                    _playbackPosition.value = exoPlayer.currentPosition
                } else if (currentSource == MusicSource.SPOTIFY) {
                    try {
                        val token = GlobalContext.Tokens.spotifyToken
                        spotifyRepository.getCurrentPlayback(token)?.let {
                            _playbackPosition.value = it.progress_ms
                            _duration.value = it.item?.duration_ms ?: 0L
                            _isPlaying.value = it.is_playing
                        }
                    } catch (e: Exception) {
                        Log.e("UnifiedAudioPlayer", "Spotify polling failed", e)
                    }
                }
                delay(1000L)
            }
        }
    }

    private fun updateMediaSessionPlaybackState() {
        val state = if (exoPlayer.isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SEEK_TO
            )
            .setState(state, exoPlayer.currentPosition, 1f)
            .build()
        mediaSession.setPlaybackState(playbackState)
    }

    override fun pause() {
        if (currentSource == MusicSource.SOUNDCLOUD) {
            exoPlayer.pause()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                spotifyRepository.pause(GlobalContext.Tokens.spotifyToken)
            }
        }
    }

    override fun resume() {
        ensurePlayerInitialized()
        if (currentSource == MusicSource.SOUNDCLOUD) {
            exoPlayer.play()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                currentTrack?.let { spotifyRepository.play(it, GlobalContext.Tokens.spotifyToken) }
            }
        }
    }

    override fun seekTo(positionMs: Long) {
        if (currentSource == MusicSource.SOUNDCLOUD) {
            exoPlayer.seekTo(positionMs)
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                spotifyRepository.seekTo(positionMs, GlobalContext.Tokens.spotifyToken)
            }
        }
    }

    override fun skipToNext() {
        if (currentSource == MusicSource.SPOTIFY) {
            CoroutineScope(Dispatchers.IO).launch {
                spotifyRepository.skipToNext(GlobalContext.Tokens.spotifyToken)
            }
        }
    }

    override fun skipToPrevious() {
        if (currentSource == MusicSource.SPOTIFY) {
            CoroutineScope(Dispatchers.IO).launch {
                spotifyRepository.skipToPrevious(GlobalContext.Tokens.spotifyToken)
            }
        }
    }

    override fun setCurrentRepeatMode(repeatMode: MusicPlayerRepeatMode) {
        _currentRepeatMode = repeatMode
        if (currentSource == MusicSource.SPOTIFY) {
            CoroutineScope(Dispatchers.IO).launch {
                spotifyRepository.setRepeatMode(repeatMode, GlobalContext.Tokens.spotifyToken)
            }
        }
    }

    override fun release() {
        positionPollingJob?.cancel()
        pollingScope.cancel()
        pollingScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

        exoPlayer.release()
        mediaSession.release()
        isReleased = true

        if (currentSource == MusicSource.SPOTIFY) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    spotifyRepository.pause(GlobalContext.Tokens.spotifyToken)
                } catch (e: Exception) {
                    Log.e("UnifiedAudioPlayer", "Spotify pause during release failed", e)
                }
            }
        }
    }

    private fun stop() {
        positionPollingJob?.cancel()
        exoPlayer.stop()
        release()
    }
}
