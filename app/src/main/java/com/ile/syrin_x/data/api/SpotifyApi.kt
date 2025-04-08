package com.ile.syrin_x.data.api

import com.ile.syrin_x.data.model.spotify.SpotifyAlbum
import com.ile.syrin_x.data.model.spotify.SpotifyAlbumByIdResponse
import com.ile.syrin_x.data.model.spotify.SpotifyPlaylistById
import com.ile.syrin_x.data.model.spotify.SpotifyResponse
import com.ile.syrin_x.data.model.spotify.SpotifyTrackDetails
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
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
}