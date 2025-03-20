package com.ile.syrin_x.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.AndroidEntryPoint
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.GetSoundCloudUserTokenUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.RefreshSoundcloudAccessToken
import com.ile.syrin_x.domain.usecase.musicsource.spotify.GetSpotifyUserTokenUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.RefreshSpotifyAccessToken
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
    private var counter = 0
    private val refreshThreshold = 3600

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
        startTokenCheckLoop()
        Log.d("TokenMonitorService", "Service started.")
    }

    private fun startForegroundService() {
        val notification = createNotification()
        startForeground(1, notification)
        Log.d("TokenMonitorService", "Foreground service started.")
    }

    private fun createNotification(): Notification {
        val channelId = "token_monitor_channel"
        val channelName = "Token Monitor Service"

        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Token Monitor Running")
            .setContentText("Monitoring token validity in the background")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    private fun startTokenCheckLoop() {
        serviceScope.launch {
            Log.d("TokenMonitorService", "Token refreshed at the start of the app.")
            checkTokenExpiry()
            Log.d("TokenMonitorService", "Token check loop started.")
            while (isActive) {
                counter++

                if (counter >= refreshThreshold) {
                    checkTokenExpiry()
                    Log.d("TokenMonitorService", "Both token will be refreshed now.")
                    counter = 0
                }

                delay(1000)
            }
        }
    }

    private suspend fun checkTokenExpiry() {
        val userUuid = getUserUidUseCase.invoke().firstOrNull() ?: return

        val spotifyToken = getSpotifyTokenUseCase(userUuid)
        val soundCloudToken = getSoundCloudTokenUseCase(userUuid)

        if(spotifyToken != null) {
            refreshSpotifyAccessToken(userUuid, spotifyToken.refreshToken)
            Log.d("TokenMonitorService", "Spotify token refreshed.")
        }
        if(soundCloudToken != null) {
            refreshSoundcloudAccessToken(userUuid, soundCloudToken.refreshToken)
            Log.d("TokenMonitorService", "SoundCloud token refreshed.")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
