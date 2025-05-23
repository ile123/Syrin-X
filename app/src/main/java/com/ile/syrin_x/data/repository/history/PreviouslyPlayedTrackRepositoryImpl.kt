package com.ile.syrin_x.data.repository.history

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ile.syrin_x.data.database.PreviouslyPlayedTrackDao
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.entity.PreviouslyPlayedTrack
import com.ile.syrin_x.data.model.entity.PreviouslyPlayedTrackEntity
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.PreviouslyPlayedTrackRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import kotlin.uuid.Uuid

class PreviouslyPlayedTrackRepositoryImpl @Inject constructor(
    private val trackDao: PreviouslyPlayedTrackDao,
    private val db: FirebaseDatabase
) : PreviouslyPlayedTrackRepository {

    override suspend fun getAllPreviouslyPlayedTracksByUser(
        userId: String
    ): Flow<Response<List<PreviouslyPlayedTrack>>> = callbackFlow {
        trySend(Response.Loading)

        val localJob = launch {
            trackDao.getAllForUser(userId)
                .map { entities ->
                    entities.map { e ->
                        PreviouslyPlayedTrack(
                            id = e.id,
                            trackId = e.trackId,
                            userId = e.userId,
                            title = e.title,
                            albumName = e.albumName,
                            artists = e.artists,
                            genre = e.genre,
                            durationMs = e.durationMs,
                            explicit = e.explicit,
                            popularity = e.popularity,
                            playbackUrl = e.playbackUrl,
                            artworkUrl = e.artworkUrl,
                            musicSource = e.musicSource,
                            timesPlayed = e.timesPlayed
                        )
                    }
                }
                .collect { tracks ->
                    trySend(Response.Success(tracks))
                }
        }

        val ref = db.getReference("users/$userId/previouslyPlayedTracks")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                launch {
                    val entities = snapshot.children.mapNotNull { snap ->
                        snap.getValue(PreviouslyPlayedTrackEntity::class.java)
                    }
                    trackDao.deleteAllForUser(userId)
                    trackDao.insertAll(entities)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addListenerForSingleValueEvent(listener)

        awaitClose {
            ref.removeEventListener(listener)
            localJob.cancel()
        }
    }

    override suspend fun getPreviouslyPlayedTrackById(
        trackId: String
    ): Flow<Response<PreviouslyPlayedTrack>> = flow {
        emit(Response.Loading)
        try {
            val e = trackDao.getById(trackId)
            if (e != null) {
                emit(Response.Success(
                    PreviouslyPlayedTrack(
                        id = e.id,
                        trackId = e.trackId,
                        userId = e.userId,
                        title = e.title,
                        albumName = e.albumName,
                        artists = e.artists,
                        genre = e.genre,
                        durationMs = e.durationMs,
                        explicit = e.explicit,
                        popularity = e.popularity,
                        playbackUrl = e.playbackUrl,
                        artworkUrl = e.artworkUrl,
                        musicSource = e.musicSource,
                        timesPlayed = e.timesPlayed
                    )
                ))
            } else {
                emit(Response.Error("PreviouslyPlayedTrack $trackId not found"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun addPreviouslyPlayedTrack(
        track: UnifiedTrack,
        userId: String
    ) {
        try {
            val existingEntity = trackDao.getById(track.id)

            val entityToSave = if (existingEntity != null) {
                val bumped = existingEntity.copy(timesPlayed = existingEntity.timesPlayed + 1)
                trackDao.update(bumped)
                bumped
            } else {
                val newEntity = PreviouslyPlayedTrackEntity(
                    id = UUID.randomUUID().toString(),
                    trackId = track.id,
                    userId = userId,
                    title = track.title,
                    albumName = track.albumName,
                    artists = track.artists,
                    genre = track.genre,
                    durationMs = track.durationMs,
                    explicit = track.explicit,
                    popularity = track.popularity,
                    playbackUrl = track.playbackUrl,
                    artworkUrl = track.artworkUrl,
                    musicSource = track.musicSource,
                    timesPlayed = 1L
                )
                trackDao.insert(newEntity)
                newEntity
            }

            db.getReference("users/$userId/previouslyPlayedTracks/${track.id}")
                .setValue(entityToSave)
                .await()

        } catch (e: Exception) {
            Log.e("PreviouslyPlayedTrackRepository", e.message ?: "Unknown error")
        }
    }

}
