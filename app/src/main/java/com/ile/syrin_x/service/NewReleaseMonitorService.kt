package com.ile.syrin_x.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.FirebaseDatabase
import com.ile.syrin_x.data.api.DeezerApi
import com.ile.syrin_x.data.database.NewReleaseNotificationDao
import com.ile.syrin_x.data.model.entity.NewReleaseNotificationEntity
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.user.GetAllFavoriteArtistsByUserUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class NewReleaseMonitorService : Service() {

    @Inject
    lateinit var getUserUidUseCase: GetUserUidUseCase

    @Inject
    lateinit var getFavoriteArtistsUseCase: GetAllFavoriteArtistsByUserUseCase

    @Inject
    lateinit var deezerApi: DeezerApi

    @Inject
    lateinit var notificationDao: NewReleaseNotificationDao

    @Inject
    lateinit var firebaseDb: FirebaseDatabase

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val ARTIST_REQUEST_DELAY_MS = 500L
    private val CHECK_INTERVAL_MS = 15 * 60 * 1000L

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        serviceScope.launch {
            while (isActive) {
                checkAllFavoritesForNewReleases()
                delay(CHECK_INTERVAL_MS)
            }
        }
    }

    private fun createNotification(): Notification {
        val channelId = "new_releases_monitor"
        val channelName = "New Releases Monitor"
        val mgr = getSystemService(NotificationManager::class.java)
        mgr?.createNotificationChannel(
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        )
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Checking for new releases…")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    private suspend fun checkAllFavoritesForNewReleases() {
        val userId = getUserUidUseCase().firstOrNull() ?: return
        val oneWeekAgo = LocalDate.now().minusDays(7)
        Log.d("NewReleaseMonitorService", "Checking if any new songs were added.")
        getFavoriteArtistsUseCase(userId)
            .collect { response ->
                when (response) {
                    is Response.Success -> {
                        val favorites = response.data
                        for (fav in favorites) {
                            val searchResponse = runCatching {
                                deezerApi.searchArtists(fav.name)
                            }.getOrNull()
                            val deezerArtist = searchResponse
                                ?.data
                                ?.firstOrNull { it.name.equals(fav.name, ignoreCase = true) }
                            if (deezerArtist == null) {
                                delay(ARTIST_REQUEST_DELAY_MS)
                                continue
                            }
                            val artistId = deezerArtist.id

                            val albumResponse = runCatching {
                                deezerApi.getArtistAlbums(artistId)
                            }.getOrNull()
                            val albums = albumResponse?.data.orEmpty()
                            if (albums.isEmpty()) {
                                delay(ARTIST_REQUEST_DELAY_MS)
                                continue
                            }

                            val recent = albums.filter {
                                LocalDate.parse(it.release_date).isAfter(oneWeekAgo)
                            }

                            for (album in recent) {
                                val albumReleaseDate = album.release_date
                                    .let { LocalDate.parse(it) }
                                    ?: LocalDate.now().minusWeeks(2)
                                val newTracks = runCatching {
                                    deezerApi.getAlbumTracks(album.id).data
                                }.getOrNull()
                                    .orEmpty()
                                    .filter {
                                        albumReleaseDate.isAfter(oneWeekAgo)
                                    }
                                for (track in newTracks) {
                                    Log.d(
                                        "NewReleaseMonitorService",
                                        "Track ${track.title} has been released."
                                    )
                                    val existing = notificationDao.getExistingNotification(track.title, artistId.toString())
                                    if (existing == null) {
                                        val notification = NewReleaseNotificationEntity(
                                            id = UUID.randomUUID().toString(),
                                            userId = userId,
                                            artistId = artistId,
                                            title = "${fav.name} – ${track.title}",
                                            timestamp = Instant.now().toEpochMilli(),
                                            seen = false
                                        )
                                        notificationDao.insert(notification)
                                        firebaseDb
                                            .getReference("users/$userId/newReleasesNotifications/${notification.id}")
                                            .setValue(notification)
                                    }
                                }
                            }

                            delay(ARTIST_REQUEST_DELAY_MS)
                        }
                    }

                    is Response.Loading -> {}
                    is Response.Error -> Log.e(
                        "NewReleaseService",
                        "Failed to load favorites: ${response.message}"
                    )
                }
            }
    }


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}