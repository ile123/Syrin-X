package com.ile.syrin_x.domain.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
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
import androidx.media2.common.MediaMetadata.METADATA_KEY_ARTIST
import androidx.media2.common.MediaMetadata.METADATA_KEY_DURATION
import androidx.media2.common.MediaMetadata.METADATA_KEY_TITLE
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.ile.syrin_x.data.enums.ShuffleMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@Singleton
class UnifiedAudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spotifyRepository: SpotifyRepository
) : AudioPlayerController {

    private var exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()
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

    private var _currentShuffleMode: ShuffleMode = ShuffleMode.OFF
    val currentShuffleMode: ShuffleMode
        get() = _currentShuffleMode

    private lateinit var notificationManager: PlayerNotificationManager
    lateinit var currentNotification: Notification
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private var pollingScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var positionPollingJob: Job? = null

    private val _onTrackEnded = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val onTrackEnded: SharedFlow<Unit> = _onTrackEnded.asSharedFlow()

    private var isServiceForeground = false

    var onSkipNext: (() -> Unit)? = null
    var onSkipPrevious: (() -> Unit)? = null

    init {
        setupMediaSession()
        setupNotification()
    }

    private fun resetPlayer() {
        positionPollingJob?.cancel()
        pollingScope.cancel()
        exoPlayer.release()
        mediaSession.release()

        pollingScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        exoPlayer = ExoPlayer.Builder(context).build().apply { addListener(playerListener) }

        mediaSession = MediaSessionCompat(context, "SyrinXMediaSession").apply { /* ... */ }
        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(exoPlayer)

        setupNotification()
        notificationManager.setPlayer(exoPlayer)
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

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(exoPlayer)

        mediaSessionConnector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession) {
            override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
                val track = currentTrack
                return MediaDescriptionCompat.Builder()
                    .setTitle(track?.title ?: "Unknown")
                    .setDescription(track?.artists?.joinToString(", ") ?: "")
                    .build()
            }
        })
    }

    private fun setupNotification() {
        val channelId = "music_playback_channel"
        val channel = NotificationChannel(
            channelId,
            "Playback",
            NotificationManager.IMPORTANCE_LOW
        )

        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)

        val builder = PlayerNotificationManager.Builder(context, 1001, channelId)
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
                    return PendingIntent.getActivity(
                        context, 0, intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
                override fun getCurrentContentText(player: Player): CharSequence? {
                    return player.mediaMetadata.artist
                }
                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
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
                override fun onNotificationPosted(
                    id: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    currentNotification = notification
                    val serviceIntent = Intent(context, MusicPlaybackService::class.java)
                        .apply { putExtra("notification_id", id) }

                    if (!isServiceForeground) {
                        ContextCompat.startForegroundService(context, serviceIntent)
                        isServiceForeground = true
                    } else {
                        context.getSystemService(NotificationManager::class.java)
                            .notify(id, notification)
                    }
                }
                override fun onNotificationCancelled(id: Int, dismissedByUser: Boolean) {
                    if (exoPlayer.isPlaying) pause()
                }
            })

        notificationManager = builder.build()

        notificationManager.setUsePlayPauseActions(true)
        notificationManager.setUsePreviousAction(true)
        notificationManager.setUseNextAction(true)
        notificationManager.setUseFastForwardAction(true)
        notificationManager.setUseRewindAction(true)
        notificationManager.setUseChronometer(true)

        notificationManager.setMediaSessionToken(mediaSession.sessionToken)
        notificationManager.setPlayer(exoPlayer)
    }

    override fun play(track: UnifiedTrack) {
        resetPlayer()

        currentTrack = track
        currentSource = track.musicSource
        val durationMs = track.durationMs ?: 0L

        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(METADATA_KEY_TITLE,  track.title)
                .putString(METADATA_KEY_ARTIST, track.artists?.joinToString(", "))
                .putLong(  METADATA_KEY_DURATION, durationMs.toLong())
                .build()
        )
        _playbackPosition.value = 0L
        _duration.value = durationMs.toLong()
        _isPlaying.value = true
        updateMediaSessionPlaybackState()

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

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(track.artists?.joinToString(", "))
            .setArtworkUri(track.artworkUrl?.toUri())
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(playbackUrl)
            .setMediaMetadata(mediaMetadata)
            .build()

        val source = ProgressiveMediaSource.Factory(
            DefaultHttpDataSource.Factory()
                .setUserAgent("SyrinX-App/1.0")
                .setDefaultRequestProperties(
                    mapOf("Authorization" to "OAuth ${GlobalContext.Tokens.soundCloudToken}")
                )
        ).createMediaSource(mediaItem)

        exoPlayer.apply {
            clearMediaItems()
            removeListener(playerListener)
            addListener(playerListener)
            setMediaSource(source)
            prepare()
            play()
            seekTo(0)
        }

        startPositionPolling()
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _isPlaying.value = isPlaying
            updateMediaSessionPlaybackState()
            startPositionPolling()
        }

        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                Player.STATE_READY -> {
                    _duration.value = exoPlayer.duration
                }
                Player.STATE_ENDED -> {
                    positionPollingJob?.cancel()

                    if (currentSource == MusicSource.SOUNDCLOUD) {
                        when (currentRepeatMode) {
                            MusicPlayerRepeatMode.ALL -> {
                                seekTo(0L)
                                exoPlayer.play()
                                startPositionPolling()
                            }
                            MusicPlayerRepeatMode.OFF -> {
                                _onTrackEnded.tryEmit(Unit)
                            }
                        }
                    } else {
                        _onTrackEnded.tryEmit(Unit)
                    }
                }
                else -> { /* do nothing */ }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e("UnifiedAudioPlayer", "Playback error", error)
        }
    }

    private fun startPositionPolling() {
        positionPollingJob?.cancel()
        positionPollingJob = pollingScope.launch {
            var lastPosition  = 0L
            var lastIsPlaying = false

            while (isActive) {
                if (currentSource == MusicSource.SOUNDCLOUD) {
                    _playbackPosition.value = exoPlayer.currentPosition
                    updateMediaSessionPlaybackState()
                } else if (currentSource == MusicSource.SPOTIFY) {
                    try {
                        val token = GlobalContext.Tokens.spotifyToken
                        spotifyRepository.getCurrentPlayback(token)?.let { playback ->
                            val pos = playback.progress_ms
                            val dur = playback.item?.duration_ms ?: 0L
                            val isPlaying = playback.is_playing

                            _playbackPosition.value = pos
                            _duration.value = dur
                            _isPlaying.value = isPlaying

                            if (
                                lastIsPlaying &&
                                !isPlaying &&
                                lastPosition >= dur - 1_000L
                            ) {
                                _onTrackEnded.tryEmit(Unit)
                                positionPollingJob?.cancel()
                                return@launch
                            }

                            lastPosition  = pos
                            lastIsPlaying = isPlaying
                        }
                    } catch (e: Exception) {
                        Log.e("UnifiedAudioPlayer", "Spotify polling failed", e)
                    }
                }
                delay(1_000L)
            }
        }
    }

    private fun updateMediaSessionPlaybackState() {
        val pos = if (currentSource == MusicSource.SOUNDCLOUD)
            exoPlayer.currentPosition
        else
            _playbackPosition.value

        val state = if (_isPlaying.value)
            PlaybackStateCompat.STATE_PLAYING
        else
            PlaybackStateCompat.STATE_PAUSED

        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                        PlaybackStateCompat.ACTION_PAUSE or
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                        PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                        PlaybackStateCompat.ACTION_SEEK_TO
            )
            .setState(state, pos, 1f)
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
                currentTrack?.let { track ->
                    val token = GlobalContext.Tokens.spotifyToken
                    spotifyRepository.resume(
                        track,
                        playbackPosition.value,
                        token
                    )
                }
                startPositionPolling()
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
        onSkipNext?.invoke()
    }

    override fun skipToPrevious() {
        onSkipPrevious?.invoke()
    }

    override fun setCurrentRepeatMode(mode: MusicPlayerRepeatMode) {
        _currentRepeatMode = mode
        if (currentSource == MusicSource.SPOTIFY) {
            CoroutineScope(Dispatchers.IO).launch {
                spotifyRepository.setRepeatMode(mode, GlobalContext.Tokens.spotifyToken)
            }
        }
    }

    override fun stop() {
        positionPollingJob?.cancel()
        exoPlayer.stop()
        release()
        Intent(context, MusicPlaybackService::class.java).also { stopIntent ->
            context.stopService(stopIntent)
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
}