package com.ile.syrin_x.data.api

import com.ile.syrin_x.data.model.deezer.MusicGenreResponse
import com.ile.syrin_x.data.model.deezer.TrackByGenreResponse
import com.ile.syrin_x.data.model.deezer.TrackReleaseAlbumResponse
import com.ile.syrin_x.data.model.deezer.TrackReleaseArtistSearchResponse
import com.ile.syrin_x.data.model.deezer.TrackReleaseTrackListResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerApi {
    @GET("genre")
    suspend fun getGenres(
        @Header("Accept-Language") language: String = "en"
    ): MusicGenreResponse

    @GET("chart/{genre_id}/tracks")
    suspend fun getTrendingTracksByGenre(
        @Path("genre_id") genreId: Long,
        @Header("Accept-Language") language: String = "en",
        @Query("index") offset: Long,
        @Query("limit") limit: Long
    ): TrackByGenreResponse

    @GET("search/artist")
    suspend fun searchArtists(
        @Query("q") query: String,
        @Header("Accept-Language") language: String = "en"
    ): TrackReleaseArtistSearchResponse

    @GET("artist/{artist_id}/albums")
    suspend fun getArtistAlbums(
        @Path("artist_id") artistId: Long,
        @Query("limit") limit: Int = 50,
        @Query("index") offset: Int = 0,
        @Header("Accept-Language") language: String = "en"
    ): TrackReleaseAlbumResponse

    @GET("album/{album_id}/tracks")
    suspend fun getAlbumTracks(
        @Path("album_id") albumId: Long,
        @Header("Accept-Language") language: String = "en"
    ): TrackReleaseTrackListResponse
}