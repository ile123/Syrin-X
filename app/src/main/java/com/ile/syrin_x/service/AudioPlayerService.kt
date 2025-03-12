package com.ile.syrin_x.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class AudioPlayerService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    private val CHANNEL_ID = "audio_playback_channel"

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Start playing music or audio here (example code)
        // mediaPlayer.setDataSource("your_audio_source") // Set the data source
        // mediaPlayer.prepareAsync() // Prepare the media player
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Music Playing")
            .setContentText("Your music is playing...")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .build()

        startForeground(1, notification)

        // Start playing music here, e.g. start the MediaPlayer
        // mediaPlayer.start()

        return START_STICKY // Keeps the service running until explicitly stopped
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

}