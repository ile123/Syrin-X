package com.ile.syrin_x.data.repository.musicsource

import android.util.Log
import com.ile.syrin_x.data.api.SpotifyApi
import com.ile.syrin_x.data.enums.MusicPlayerRepeatMode
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.spotify.PlaybackRequestBody
import com.ile.syrin_x.data.model.spotify.SpotifyAlbumByIdResponse
import com.ile.syrin_x.data.model.spotify.SpotifyPlaylistById
import com.ile.syrin_x.data.model.spotify.SpotifyResponse
import com.ile.syrin_x.data.model.spotify.SpotifyTrackDetails
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.SpotifyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SpotifyRepositoryImpl @Inject constructor(
    private val api: SpotifyApi
): SpotifyRepository {
    override suspend fun searchAll(keyword: String, accessToken: String): Flow<Response<SpotifyResponse>> = flow {
        try {
            emit(Response.Loading)
            val data = api.getContent("Bearer $accessToken", keyword, "track,album,playlist,artist", "HR", 20, 0)
            emit(Response.Success(data))
        } catch (e: Exception) {
            Log.d("SpotifyRepository Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun searchTracks(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<SpotifyResponse>> = flow {
        try {
            emit(Response.Loading)
            val data = api.getContent("Bearer $accessToken", keyword, "track", "HR", limit, offset)
            emit(Response.Success(data))
        } catch (e: Exception) {
            Log.d("SpotifyRepository Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun searchAlbums(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<SpotifyResponse>> = flow {
        try {
            emit(Response.Loading)
            val data = api.getContent("Bearer $accessToken", keyword, "album", "HR", limit, offset)
            emit(Response.Success(data))
        } catch (e: Exception) {
            Log.d("SpotifyRepository Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun searchPlaylists(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<SpotifyResponse>> = flow {
        try {
            emit(Response.Loading)
            val data = api.getContent("Bearer $accessToken", keyword, "playlist", "HR", limit, offset)
            emit(Response.Success(data))
        } catch (e: Exception) {
            Log.d("SpotifyRepository Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun searchArtists(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<SpotifyResponse>> = flow {
        try {
            emit(Response.Loading)
            val data = api.getContent("Bearer $accessToken", keyword, "artist", "HR", limit, offset)
            emit(Response.Success(data))
        } catch (e: Exception) {
            Log.d("SpotifyRepository Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun searchTrackById(
        id: String,
        accessToken: String
    ): Flow<Response<SpotifyTrackDetails>> = flow {
        try {
            emit(Response.Loading)
            val data = api.getTrackById("Bearer $accessToken", id)
            emit(Response.Success(data))
        } catch (e: Exception) {
            Log.d("SpotifyRepository Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun searchPlaylistById(
        id: String,
        accessToken: String
    ): Flow<Response<SpotifyPlaylistById>> = flow {
        try {
            emit(Response.Loading)
            val data = api.getPlaylistById("Bearer $accessToken", id)
            emit(Response.Success(data))
        } catch (e: Exception) {
            Log.d("SpotifyRepository Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun searchAlbumById(
        id: String,
        accessToken: String
    ): Flow<Response<SpotifyAlbumByIdResponse>> = flow {
        try {
            emit(Response.Loading)
            val data = api.getAlbumById("Bearer $accessToken", id)
            emit(Response.Success(data))
        } catch (e: Exception) {
            Log.d("SpotifyRepository Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun play(track: UnifiedTrack, accessToken: String, deviceId: String?) {
        try {
            val body = PlaybackRequestBody(uris = listOf(track.playbackUrl))
            val response = api.startPlayback("Bearer $accessToken", deviceId, body)
            if (!response.isSuccessful) {
                Log.e("SpotifyPlayback", "Play failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("SpotifyPlayback", "Play failed: ${e.message}")
        }
    }

    override suspend fun pause(accessToken: String, deviceId: String?) {
        try {
            val response = api.pausePlayback("Bearer $accessToken", deviceId)
            if (!response.isSuccessful) {
                Log.e("SpotifyPlayback", "Pause failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("SpotifyPlayback", "Pause failed: ${e.message}")
        }
    }

    override suspend fun seekTo(positionMs: Long, accessToken: String, deviceId: String?) {
        try {
            val response = api.seekToPosition("Bearer $accessToken", positionMs, deviceId)
            if (!response.isSuccessful) {
                Log.e("SpotifyPlayback", "Seek failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("SpotifyPlayback", "Seek failed: ${e.message}")
        }
    }

    override suspend fun skipToNext(accessToken: String, deviceId: String?) {
        try {
            val response = api.skipNext("Bearer $accessToken", deviceId)
            if (!response.isSuccessful) {
                Log.e("SpotifyPlayback", "Skip to next failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("SpotifyPlayback", "Skip to next failed: ${e.message}")
        }
    }

    override suspend fun skipToPrevious(accessToken: String, deviceId: String?) {
        try {
            val response = api.skipPrevious("Bearer $accessToken", deviceId)
            if (!response.isSuccessful) {
                Log.e("SpotifyPlayback", "Skip to previous failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("SpotifyPlayback", "Skip to previous failed: ${e.message}")
        }
    }

    override suspend fun setRepeatMode(mode: MusicPlayerRepeatMode, accessToken: String, deviceId: String?) {
        val apiState = when (mode) {
            MusicPlayerRepeatMode.OFF -> "off"
            MusicPlayerRepeatMode.ONE -> "track"
            MusicPlayerRepeatMode.ALL -> "context"
        }

        try {
            val response = api.setRepeatMode("Bearer $accessToken", apiState, deviceId)
            if (!response.isSuccessful) {
                Log.e("SpotifyPlayback", "Set repeat mode failed: ${response.code()} ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("SpotifyPlayback", "Set repeat mode failed: ${e.message}")
        }
    }


}