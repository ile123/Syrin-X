package com.ile.syrin_x.data.repository.usercreatedplaylist

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ile.syrin_x.data.database.FavoriteTrackDao
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.entity.FavoriteTrackEntity
import com.ile.syrin_x.data.model.usercreatedplaylist.FavoriteTrack
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.FavoriteTrackRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class FavoriteTrackRepositoryImpl @Inject constructor(
    private val favoriteTrackDao: FavoriteTrackDao,
    private val db: FirebaseDatabase
): FavoriteTrackRepository {
    override suspend fun getAllFavoriteTracksByUser(
        userId: String
    ): Flow<Response<List<FavoriteTrack>>> = callbackFlow {
        trySend(Response.Loading)

        val dbJob = launch {
            favoriteTrackDao
                .getAllForUser(userId)
                .map { entities ->
                    entities.map { e ->
                        FavoriteTrack(
                            favoriteTrackId = e.favoriteTrackId,
                            trackId = e.trackId,
                            userId = e.userId,
                            name = e.name,
                            artist = e.artist,
                            musicSource = e.musicSource
                        )
                    }
                }
                .collect { tracks ->
                    trySend(Response.Success(tracks))
                }
        }

        val favRef = db
            .getReference("users")
            .child(userId)
            .child("favoriteTracks")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entities = snapshot.children.mapNotNull { snap ->
                    snap.getValue(FavoriteTrackEntity::class.java)
                }
                launch {
                    favoriteTrackDao.deleteAllForUser(userId)
                    favoriteTrackDao.insertAll(entities)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        favRef.addListenerForSingleValueEvent(listener)

        awaitClose {
            dbJob.cancel()
            favRef.removeEventListener(listener)
        }
    }

    override suspend fun getFavoriteTrackById(
        favoriteTrackId: String
    ): Flow<Response<FavoriteTrack>> = flow {
        emit(Response.Loading)
        try {
            val entity = favoriteTrackDao.getById(favoriteTrackId)
            if (entity != null) {
                val track = FavoriteTrack(
                    favoriteTrackId = entity.favoriteTrackId,
                    trackId = entity.trackId,
                    userId = entity.userId,
                    name = entity.name,
                    artist = entity.artist,
                    musicSource = entity.musicSource
                )
                emit(Response.Success(track))
            } else {
                emit(Response.Error("FavoriteTrack $favoriteTrackId not found"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun addOrRemoveTrackFromFavorites(
        track: UnifiedTrack,
        userId: String
    ): Flow<Response<FavoriteTrack>> = flow {
        emit(Response.Loading)
        try {
            val dbRef = db
                .getReference("users")
                .child(userId)
                .child("favoriteTracks")
                .child(track.id)

            val existing = favoriteTrackDao.getById(track.id)
            if (existing != null) {
                favoriteTrackDao.delete(existing)
                dbRef.removeValue().await()
                val removed = FavoriteTrack(
                    favoriteTrackId = existing.favoriteTrackId,
                    trackId = track.id,
                    userId = existing.userId,
                    name = existing.name,
                    artist = existing.artist,
                    musicSource = existing.musicSource
                )
                emit(Response.Success(removed))
            } else {
                val newEntity = track.title?.let { title ->
                    track.artists?.get(0)?.let { artist ->
                        FavoriteTrackEntity(
                            favoriteTrackId = track.id,
                            trackId = track.id,
                            userId = userId,
                            name = title,
                            artist = artist,
                            musicSource = track.musicSource
                        )
                    }
                }
                if (newEntity != null) {
                    favoriteTrackDao.insert(newEntity)
                }
                dbRef.setValue(newEntity).await()
                val added = FavoriteTrack(
                    favoriteTrackId = newEntity!!.favoriteTrackId,
                    trackId = track.id,
                    userId = newEntity.userId,
                    name = newEntity.name,
                    artist = newEntity.artist,
                    musicSource = newEntity.musicSource
                )
                emit(Response.Success(added))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.message.toString()))
        }
    }
}