package com.ile.syrin_x.domain.player

import android.content.Context
import android.util.Log
import com.ile.syrin_x.data.enums.MusicPlayerRepeatMode
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.utils.EnvLoader
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Repeat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class SpotifyRemoteClient(private val context: Context) {
    private var spotifyAppRemote: SpotifyAppRemote? = null

    suspend fun connect(): Result<Unit> = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val connectionParams = ConnectionParams.Builder(EnvLoader.spotifyClientId)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build()

            SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
                override fun onConnected(remote: SpotifyAppRemote) {
                    spotifyAppRemote = remote
                    continuation.resume(Result.success(Unit))
                }

                override fun onFailure(error: Throwable) {
                    continuation.resume(Result.failure(error))
                }
            })
        }
    }


    fun play(track: UnifiedTrack) {
        val uri = track.playbackUrl ?: return
        spotifyAppRemote?.playerApi?.play(uri)
    }

    fun pause() = spotifyAppRemote?.playerApi?.pause()
    fun resume() = spotifyAppRemote?.playerApi?.resume()
    fun seekTo(positionMs: Long) = spotifyAppRemote?.playerApi?.seekTo(positionMs)
    fun skipToNext() = spotifyAppRemote?.playerApi?.skipNext()
    fun skipToPrevious() = spotifyAppRemote?.playerApi?.skipPrevious()

    fun setRepeatMode(repeatMode: MusicPlayerRepeatMode) {
        val mode = when (repeatMode) {
            MusicPlayerRepeatMode.OFF -> Repeat.OFF
            MusicPlayerRepeatMode.ONE -> Repeat.ONE
            MusicPlayerRepeatMode.ALL -> Repeat.ALL
        }
        spotifyAppRemote?.playerApi?.setRepeat(mode)
    }

    fun isConnected(): Boolean {
        return spotifyAppRemote?.isConnected == true
    }

    fun disconnect() {
        spotifyAppRemote?.let { SpotifyAppRemote.disconnect(it) }
    }

    companion object {
        const val REDIRECT_URI = "syrinx://app/spotify"
    }
}