package com.ile.syrin_x.data.repository.usercreatedplaylist

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ile.syrin_x.data.database.UserCreatedPlaylistDao
import com.ile.syrin_x.data.database.UserCreatedPlaylistTrackDao
import com.ile.syrin_x.data.enums.UserCreatedPlaylistTrackAction
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.entity.PlaylistWithTracks
import com.ile.syrin_x.data.model.entity.UserCreatedPlaylistEntity
import com.ile.syrin_x.data.model.entity.UserCreatedPlaylistTrackEntity
import com.ile.syrin_x.data.model.usercreatedplaylist.UserCreatedPlaylist
import com.ile.syrin_x.data.model.usercreatedplaylist.UserCreatedPlaylistTrack
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.UserCreatedPlaylistRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject

class UserCreatedPlaylistRepositoryImpl @Inject constructor(
    private val playlistDao: UserCreatedPlaylistDao,
    private val trackDao: UserCreatedPlaylistTrackDao,
    private val db: FirebaseDatabase
) : UserCreatedPlaylistRepository {

    override suspend fun getAllUserCreatedPlaylistsByUser(
        userId: String
    ): Flow<Response<List<UserCreatedPlaylist>>> = callbackFlow {
        trySend(Response.Loading)

        val localJob = launch {
            playlistDao.getAllForUserWithTracks(userId).collect { pwList ->
                val domain = pwList.map { it.toDomain() }
                trySend(Response.Success(domain))
            }
        }

        val ref = db.getReference("users/$userId/playlists")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                launch {
                    val existing = playlistDao.getAllForUserWithTracks(userId).first()
                    existing.forEach { pw ->
                        pw.tracks.forEach { trackDao.deleteTrack(it) }
                        playlistDao.deletePlaylist(pw.playlist)
                    }

                    val remotePlaylists = snapshot.children.mapNotNull { snap ->
                        snap.getValue(RemotePlaylist::class.java)
                    }
                    remotePlaylists.forEach { rp ->
                        val pe = UserCreatedPlaylistEntity(
                            userCreatedPlaylistId = rp.userCreatedPlaylistId,
                            userId = rp.userId,
                            name = rp.name,
                            dateAdded = LocalDate.parse(rp.dateAdded)
                        )
                        playlistDao.insertPlaylist(pe)
                        rp.tracks.forEach { rt ->
                            val te = UserCreatedPlaylistTrackEntity(
                                userCreatedPlaylistTrackId = rt.userCreatedPlaylistTrackId,
                                playlistId = rp.userCreatedPlaylistId,
                                trackId = rt.trackId,
                                title = rt.title,
                                albumName = rt.albumName,
                                artists = rt.artists,
                                genre = rt.genre,
                                durationMs = rt.durationMs,
                                explicit = rt.explicit,
                                popularity = rt.popularity,
                                playbackUrl = rt.playbackUrl,
                                artworkUrl = rt.artworkUrl,
                                musicSource = rt.musicSource,
                                timePlayed = rt.timePlayed,
                                dateCreated = LocalDate.parse(rt.dateCreated)
                            )
                            trackDao.insertTrack(te)
                        }
                    }
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

    override suspend fun getUserCreatedPlaylistById(
        playlistId: String
    ): Flow<Response<UserCreatedPlaylist>> = flow {
        emit(Response.Loading)
        try {
            val pw = playlistDao.getByIdWithTracks(playlistId).first() ?: run {
                emit(Response.Error("Playlist $playlistId not found"))
                return@flow
            }
            emit(Response.Success(pw.toDomain()))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun createUserCreatedPlaylist(
        name: String,
        userId: String
    ): Flow<Response<List<UserCreatedPlaylist>>> = flow {
        emit(Response.Loading)
        try {
            val pushRef = db.getReference("users/$userId/playlists").push()
            val newId = pushRef.key!!
            val nowStr = LocalDate.now().toString()

            val remote = RemotePlaylist(
                userCreatedPlaylistId = newId,
                userId = userId,
                name = name,
                dateAdded = nowStr,
                tracks = emptyList()
            )
            playlistDao.insertPlaylist(
                UserCreatedPlaylistEntity(
                    userCreatedPlaylistId = newId,
                    userId = userId,
                    name = name,
                    dateAdded = LocalDate.parse(nowStr)
                )
            )
            pushRef.setValue(remote).await()

            val all = playlistDao.getAllForUserWithTracks(userId).first().map { it.toDomain() }
            emit(Response.Success(all))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun updateUserCreatedPlaylist(
        newName: String,
        playlistId: String
    ): Flow<Response<UserCreatedPlaylist>> = flow {
        emit(Response.Loading)
        try {
            val pw = playlistDao.getByIdWithTracks(playlistId).first() ?: return@flow emit(Response.Error("Playlist $playlistId not found"))
            val updatedEntity = pw.playlist.copy(name = newName)
            playlistDao.updatePlaylist(updatedEntity)

            db.getReference("users/${pw.playlist.userId}/playlists/$playlistId/name")
                .setValue(newName).await()

            emit(Response.Success(playlistDao.getByIdWithTracks(playlistId).first()!!.toDomain()))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun addOrRemoveUserCreatedPlaylistTrack(
        track: UnifiedTrack,
        playlistId: String,
        action: UserCreatedPlaylistTrackAction
    ): Flow<Response<UserCreatedPlaylist>> = flow {
        emit(Response.Loading)
        try {
            val pw = playlistDao.getByIdWithTracks(playlistId).first() ?: return@flow emit(Response.Error("Playlist $playlistId not found"))
            val userId = pw.playlist.userId
            val ref = db.getReference("users/$userId/playlists/$playlistId/tracks")
            val nowStr = LocalDate.now().toString()

            when (action) {
                UserCreatedPlaylistTrackAction.ADD_TRACK -> {
                    val remoteTrack = RemotePlaylistTrack(
                        userCreatedPlaylistTrackId = track.id,
                        trackId = track.id,
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
                        timePlayed = 1L,
                        dateCreated = nowStr
                    )
                    trackDao.insertTrack(
                        UserCreatedPlaylistTrackEntity(
                            userCreatedPlaylistTrackId = remoteTrack.userCreatedPlaylistTrackId,
                            playlistId = playlistId,
                            trackId = remoteTrack.trackId,
                            title = remoteTrack.title,
                            albumName = remoteTrack.albumName,
                            artists = remoteTrack.artists,
                            genre = remoteTrack.genre,
                            durationMs = remoteTrack.durationMs,
                            explicit = remoteTrack.explicit,
                            popularity = remoteTrack.popularity,
                            playbackUrl = remoteTrack.playbackUrl,
                            artworkUrl = remoteTrack.artworkUrl,
                            musicSource = remoteTrack.musicSource,
                            timePlayed = remoteTrack.timePlayed,
                            dateCreated = LocalDate.parse(nowStr)
                        )
                    )
                    ref.child(track.id).setValue(remoteTrack).await()
                }
                UserCreatedPlaylistTrackAction.REMOVE_TRACK -> {
                    pw.tracks.find { it.userCreatedPlaylistTrackId == track.id }?.let {
                        trackDao.deleteTrack(it)
                    }
                    ref.child(track.id).removeValue().await()
                }
            }

            val updatedPw = playlistDao.getByIdWithTracks(playlistId).first()!!
            emit(Response.Success(updatedPw.toDomain()))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    override suspend fun deleteUserCreatedPlaylist(
        playlistId: String
    ): Flow<Response<UserCreatedPlaylist>> = flow {
        emit(Response.Loading)
        try {
            val pw = playlistDao.getByIdWithTracks(playlistId).first() ?: return@flow emit(Response.Error("Playlist $playlistId not found"))
            playlistDao.deletePlaylist(pw.playlist)
            db.getReference("users/${pw.playlist.userId}/playlists/$playlistId").removeValue().await()
            emit(Response.Success(pw.toDomain()))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Unknown error"))
        }
    }

    data class RemotePlaylist(
        val userCreatedPlaylistId: String = "",
        val userId: String = "",
        val name: String = "",
        val dateAdded: String = "",
        val tracks: List<RemotePlaylistTrack> = emptyList()
    )

    data class RemotePlaylistTrack(
        val userCreatedPlaylistTrackId: String = "",
        val trackId: String = "",
        val title: String? = null,
        val albumName: String? = null,
        val artists: List<String>? = null,
        val genre: String? = null,
        val durationMs: Int? = null,
        val explicit: Boolean? = null,
        val popularity: Int? = null,
        val playbackUrl: String? = null,
        val artworkUrl: String? = null,
        val musicSource: com.ile.syrin_x.data.enums.MusicSource = com.ile.syrin_x.data.enums.MusicSource.SPOTIFY,
        val timePlayed: Long = 0L,
        val dateCreated: String = ""
    )

    private fun PlaylistWithTracks.toDomain(): UserCreatedPlaylist =
        UserCreatedPlaylist(
            userCreatedPlaylistId = playlist.userCreatedPlaylistId,
            userId = playlist.userId,
            name = playlist.name,
            tracks = tracks.map { te ->
                UserCreatedPlaylistTrack(
                    userCreatedPlaylistTrackId = te.userCreatedPlaylistTrackId,
                    trackId = te.trackId,
                    title = te.title,
                    albumName = te.albumName,
                    artists = te.artists,
                    genre = te.genre,
                    durationMs = te.durationMs,
                    explicit = te.explicit,
                    popularity = te.popularity,
                    playbackUrl = te.playbackUrl,
                    artworkUrl = te.artworkUrl,
                    musicSource = te.musicSource,
                    timePlayed = te.timePlayed
                )
            }
        )
    }