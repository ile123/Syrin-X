package com.ile.syrin_x.data.api

import com.ile.syrin_x.data.model.deezer.MusicGenreResponse
import com.ile.syrin_x.data.model.deezer.TrackByGenreResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerApi {
    @GET("genre")
    suspend fun getGenres(): MusicGenreResponse

    @GET("chart/{genre_id}/tracks")
    suspend fun getTrendingTracksByGenre(
        @Path("genre_id") genreId: Long,
        @Query("index") offset: Long,
        @Query("limit") limit: Long
    ): TrackByGenreResponse
}