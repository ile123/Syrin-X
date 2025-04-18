package com.ile.syrin_x.domain.repository

import com.ile.syrin_x.data.enums.MusicPlayerRepeatMode
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.spotify.SpotifyAlbum
import com.ile.syrin_x.data.model.spotify.SpotifyAlbumByIdResponse
import com.ile.syrin_x.data.model.spotify.SpotifyPlaylistById
import com.ile.syrin_x.data.model.spotify.SpotifyResponse
import com.ile.syrin_x.data.model.spotify.SpotifyTrackDetails
import com.ile.syrin_x.domain.core.Response
import kotlinx.coroutines.flow.Flow

interface SpotifyRepository {
    suspend fun searchAll(keyword: String, accessToken: String): Flow<Response<Any>>
    suspend fun searchTracks(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<SpotifyResponse>>
    suspend fun searchAlbums(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<SpotifyResponse>>
    suspend fun searchPlaylists(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<SpotifyResponse>>
    suspend fun searchArtists(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<SpotifyResponse>>
    suspend fun searchTrackById(id: String, accessToken: String): Flow<Response<SpotifyTrackDetails>>
    suspend fun searchPlaylistById(id: String, accessToken: String): Flow<Response<SpotifyPlaylistById>>
    suspend fun searchAlbumById(id: String, accessToken: String): Flow<Response<SpotifyAlbumByIdResponse>>
    suspend fun play(track: UnifiedTrack, accessToken: String, deviceId: String?)
    suspend fun pause(accessToken: String, deviceId: String?)
    suspend fun seekTo(positionMs: Long, accessToken: String, deviceId: String?)
    suspend fun skipToNext(accessToken: String, deviceId: String?)
    suspend fun skipToPrevious(accessToken: String, deviceId: String?)
    suspend fun setRepeatMode(mode: MusicPlayerRepeatMode, accessToken: String, deviceId: String?)

}