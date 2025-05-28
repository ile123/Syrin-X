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
import com.ile.syrin_x.data.model.entity.FavoriteArtist
import com.ile.syrin_x.data.model.entity.NewReleaseNotificationEntity
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.user.GetAllFavoriteArtistsByUserUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
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
    private val CHECK_INTERVAL_MS = 15 * 60 * 1_000L
    private val MAX_CONCURRENT_ARTIST_CALLS = 4

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())

        serviceScope.launch {
            while (isActive) {
                try {
                    checkAllFavoritesForNewReleases()
                } catch (t: Throwable) {
                    Log.e("NewReleaseMonitorSvc", "Error during check, will retry", t)
                }
                delay(CHECK_INTERVAL_MS)
            }
        }
    }

    private fun createNotification(): Notification {
        val channelId = "new_releases_monitor"
        val mgr = getSystemService(NotificationManager::class.java)
        mgr?.createNotificationChannel(
            NotificationChannel(
                channelId,
                "New Releases Monitor",
                NotificationManager.IMPORTANCE_LOW
            )
        )
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Checking for new releases…")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private suspend fun checkAllFavoritesForNewReleases() = coroutineScope {
        val userId = getUserUidUseCase()
            .firstOrNull()
            ?: return@coroutineScope

        val cutoffDate = LocalDate.now().minusDays(7)

        Log.d("NewReleaseMonitorSvc", "Checking for new releases…")

        val favorites = getFavoriteArtistsUseCase(userId)
            .filter { it is Response.Success }
            .map { (it as Response.Success<List<FavoriteArtist>>).data }
            .firstOrNull()
            .orEmpty()

        if (favorites.isEmpty()) return@coroutineScope

        val tickerChannel = ticker(
            delayMillis = ARTIST_REQUEST_DELAY_MS,
            initialDelayMillis = 0
        )

        val semaphore = Semaphore(MAX_CONCURRENT_ARTIST_CALLS)

        favorites.map { fav ->
            async {
                semaphore.withPermit {
                    val deezerArtist = runCatching {
                        tickerChannel.receive()
                        deezerApi.searchArtists(fav.name)
                    }.getOrNull()
                        ?.data
                        ?.firstOrNull { it.name.equals(fav.name, ignoreCase = true) }

                    if (deezerArtist == null) return@withPermit

                    val artistId = deezerArtist.id

                    val albums = runCatching {
                        tickerChannel.receive()
                        deezerApi.getArtistAlbums(artistId).data
                    }.getOrNull().orEmpty()

                    val recentAlbums = albums.filter { album ->
                        LocalDate.parse(album.release_date).let { !it.isBefore(cutoffDate) }
                    }

                    for (album in recentAlbums) {
                        val albumDate = LocalDate.parse(album.release_date)

                        val tracks = runCatching {
                            tickerChannel.receive()
                            deezerApi.getAlbumTracks(album.id).data
                        }.getOrNull().orEmpty()

                        tracks.filter {
                            !albumDate.isBefore(cutoffDate)
                        }.forEach { track ->
                            Log.d("NewReleaseMonitorSvc", "New track: ${track.title}")

                            val existing = notificationDao.getExistingNotification(
                                track.title,
                                artistId
                            )
                            if (existing == null) {
                                val notification = NewReleaseNotificationEntity(
                                    userId = userId,
                                    trackId = track.id,
                                    artistId = artistId,
                                    title = "${fav.name} – ${track.title}",
                                    timestamp = Instant.now().toEpochMilli(),
                                    seen = false
                                )
                                notificationDao.insert(notification)
                                firebaseDb
                                    .getReference("users/$userId/newReleasesNotifications/${track.id}")
                                    .setValue(notification)
                            }
                        }
                    }
                }
            }
        }
            .awaitAll()

        tickerChannel.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}