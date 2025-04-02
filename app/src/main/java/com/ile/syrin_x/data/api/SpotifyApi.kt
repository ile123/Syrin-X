package com.ile.syrin_x.data.api

import com.ile.syrin_x.data.model.spotify.SpotifyResponse
import retrofit2.http.GET
import retrofit2.http.Header
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
}