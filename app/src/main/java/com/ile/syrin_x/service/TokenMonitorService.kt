package com.ile.syrin_x.service

import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.AndroidEntryPoint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.GetSoundCloudUserTokenUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.RefreshSoundcloudAccessToken
import com.ile.syrin_x.domain.usecase.musicsource.spotify.GetSpotifyUserTokenUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.RefreshSpotifyAccessToken
import com.ile.syrin_x.utils.GlobalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TokenMonitorService : Service() {

    @Inject
    lateinit var getUserUidUseCase: GetUserUidUseCase

    @Inject
    lateinit var getSpotifyTokenUseCase: GetSpotifyUserTokenUseCase

    @Inject
    lateinit var getSoundCloudTokenUseCase: GetSoundCloudUserTokenUseCase

    @Inject
    lateinit var refreshSpotifyAccessToken: RefreshSpotifyAccessToken

    @Inject
    lateinit var refreshSoundcloudAccessToken: RefreshSoundcloudAccessToken

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        startTokenCheckLoop()
        Log.d("TokenMonitorService", "Service started.")
    }

    private fun startForegroundService() {
        val channelId = "token_monitor_channel"
        val channelName = "Token Monitor Service"
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java)
            ?.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Token Monitor Running")
            .setContentText("Monitoring token validity in the background")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        startForeground(1, notification)
        Log.d("TokenMonitorService", "Foreground service started.")
    }

    private fun startTokenCheckLoop() {
        serviceScope.launch {
            Log.d("TokenMonitorService", "Initial token check.")
            checkTokenExpiry()
            while (isActive) {
                delay(60000)
                checkTokenExpiry()
            }
        }
        Log.d("TokenMonitorService", "Token check loop started.")
    }

    private suspend fun checkTokenExpiry() {
        val userUuid = getUserUidUseCase().firstOrNull()
            ?: return

        getSpotifyTokenUseCase(userUuid)?.let { token ->
            val nowSec = System.currentTimeMillis() / 1000
            if (nowSec >= token.expiresAt - 60) {
                Log.d("TokenMonitorService", "Refreshing Spotify token for $userUuid")
                refreshSpotifyAccessToken(userUuid, token.refreshToken)
            }
            GlobalContext.Tokens.spotifyToken = token.accessToken
            GlobalContext.loggedInMusicSources.addIfMissing("Spotify")
        }

        getSoundCloudTokenUseCase(userUuid)?.let { token ->
            val nowSec = System.currentTimeMillis() / 1000
            if (nowSec >= token.expiresAt - 60) {
                Log.d("TokenMonitorService", "Refreshing SoundCloud token for $userUuid")
                refreshSoundcloudAccessToken(userUuid, token.refreshToken)
            }
            GlobalContext.Tokens.soundCloudToken = token.accessToken
            GlobalContext.loggedInMusicSources.addIfMissing("SoundCloud")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

private fun MutableList<String>.addIfMissing(source: String) {
    if (!contains(source)) add(source)
}
