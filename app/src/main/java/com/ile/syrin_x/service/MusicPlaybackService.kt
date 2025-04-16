package com.ile.syrin_x.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ile.syrin_x.domain.player.UnifiedAudioPlayer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlaybackService : Service() {

    @Inject
    lateinit var unifiedAudioPlayer: UnifiedAudioPlayer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationId = intent?.getIntExtra("notification_id", -1) ?: -1
        if (notificationId != -1) {
            val notification = unifiedAudioPlayer.currentNotification
            startForeground(notificationId, notification)
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopForeground(true)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
    }
}
