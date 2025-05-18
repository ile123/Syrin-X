package com.ile.syrin_x.data.repository.usercreatedplaylist

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ile.syrin_x.data.database.FavoriteArtistDao
import com.ile.syrin_x.data.model.entity.FavoriteArtist
import com.ile.syrin_x.data.model.entity.FavoriteArtistEntity
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.FavoriteArtistRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FavoriteArtistRepositoryImpl @Inject constructor(
    private val favoriteArtistDao: FavoriteArtistDao,
    private val db: FirebaseDatabase
) : FavoriteArtistRepository {

    override suspend fun getAllUsersFavoriteArtists(
        userId: String
    ): Flow<Response<List<FavoriteArtist>>> = callbackFlow {
        trySend(Response.Loading)

        val artists = favoriteArtistDao.getAllForUser(userId)
            .map { entities ->
                entities.map { e ->
                    FavoriteArtist(id = e.id, userId = e.userId, name = e.name)
                }
            }
            .first()
        trySend(Response.Success(artists))

        val ref = db.getReference("users/$userId/favoriteArtists")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                launch {
                    val entities =
                        snapshot.children.mapNotNull { it.getValue(FavoriteArtistEntity::class.java) }
                    favoriteArtistDao.deleteAllForUser(userId)
                    favoriteArtistDao.insertAll(entities)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addListenerForSingleValueEvent(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }


    override suspend fun getFavoriteArtistById(
        userId: String,
        artistId: String
    ): Flow<Response<FavoriteArtist>> = flow {
        emit(Response.Loading)
        try {
            val entity = favoriteArtistDao.getById(artistId, userId)
            if (entity != null) {
                val artist = FavoriteArtist(
                    id = entity.id,
                    userId = entity.userId,
                    name = entity.name
                )
                emit(Response.Success(artist))
            } else {
                emit(Response.Error("FavoriteArtist $artistId not found"))
            }
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun addOrRemoveFavoriteArtist(artist: FavoriteArtist): Flow<Response<FavoriteArtist>> =
        flow {
            emit(Response.Loading)
            try {
                val entity = FavoriteArtistEntity(
                    id = artist.id,
                    userId = artist.userId,
                    name = artist.name
                )

                val existing = favoriteArtistDao.getById(artist.id, artist.userId)

                if (existing != null) {
                    favoriteArtistDao.delete(entity)
                    db.getReference("users/${artist.userId}/favoriteArtists/${artist.id}")
                        .removeValue()
                        .await()
                } else {
                    favoriteArtistDao.insert(entity)
                    db.getReference("users/${artist.userId}/favoriteArtists/${artist.id}")
                        .setValue(entity)
                        .await()
                }

                emit(Response.Success(artist))
            } catch (e: Exception) {
                emit(Response.Error(e.localizedMessage ?: "Unknown error"))
            }
        }

}
