package com.ile.syrin_x.data.api

import com.ile.syrin_x.data.model.spotify.PlaybackRequestBody
import com.ile.syrin_x.data.model.spotify.SpotifyAlbum
import com.ile.syrin_x.data.model.spotify.SpotifyAlbumByIdResponse
import com.ile.syrin_x.data.model.spotify.SpotifyPlaybackState
import com.ile.syrin_x.data.model.spotify.SpotifyPlaylistById
import com.ile.syrin_x.data.model.spotify.SpotifyResponse
import com.ile.syrin_x.data.model.spotify.SpotifyTrackDetails
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SpotifyApi {
    @GET("v1/search")
    suspend fun getContent(
        @Header("Authorization") authorization: String,
        @Query("q") q: String,
        @Query("type") type: String,
        @Query("market") market: String,
        @Query("limit") limit: Long,
        @Query("offset") offset: Long
    ): SpotifyResponse

    @GET("v1/tracks/{id}")
    suspend fun getTrackById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): SpotifyTrackDetails

    @GET("v1/playlists/{id}")
    suspend fun getPlaylistById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): SpotifyPlaylistById

    @GET("v1/albums/{id}")
    suspend fun getAlbumById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): SpotifyAlbumByIdResponse

    @PUT("me/player/play")
    suspend fun startPlayback(
        @Header("Authorization") token: String,
        @Query("device_id") deviceId: String? = null,
        @Body body: PlaybackRequestBody
    ): Response<Unit>

    @PUT("me/player/pause")
    suspend fun pausePlayback(
        @Header("Authorization") token: String,
        @Query("device_id") deviceId: String? = null
    ): Response<Unit>

    @POST("me/player/next")
    suspend fun skipNext(
        @Header("Authorization") token: String,
        @Query("device_id") deviceId: String? = null
    ): Response<Unit>

    @POST("me/player/previous")
    suspend fun skipPrevious(
        @Header("Authorization") token: String,
        @Query("device_id") deviceId: String? = null
    ): Response<Unit>

    @PUT("me/player/repeat")
    suspend fun setRepeatMode(
        @Header("Authorization") token: String,
        @Query("state") state: String,
        @Query("device_id") deviceId: String? = null
    ): Response<Unit>

    @PUT("me/player/seek")
    suspend fun seekToPosition(
        @Header("Authorization") token: String,
        @Query("position_ms") positionMs: Long,
        @Query("device_id") deviceId: String? = null
    ): Response<Unit>

    @GET("me/player")
    suspend fun getCurrentPlayback(
        @Header("Authorization") token: String
    ): Response<SpotifyPlaybackState>
}