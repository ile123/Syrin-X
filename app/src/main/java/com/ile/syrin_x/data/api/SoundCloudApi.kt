package com.ile.syrin_x.data.api

import com.ile.syrin_x.data.model.soundcloud.SoundCloudBpm
import com.ile.syrin_x.data.model.soundcloud.SoundCloudCreatedAt
import com.ile.syrin_x.data.model.soundcloud.SoundCloudDuration
import com.ile.syrin_x.data.model.soundcloud.SoundCloudPlaylistById
import com.ile.syrin_x.data.model.soundcloud.SoundCloudPlaylistResponse
import com.ile.syrin_x.data.model.soundcloud.SoundCloudTrackById
import com.ile.syrin_x.data.model.soundcloud.SoundCloudTrackResponse
import com.ile.syrin_x.data.model.soundcloud.SoundCloudTrackStreamableUrls
import com.ile.syrin_x.data.model.soundcloud.SoundCloudUserResponse
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SoundCloudApi {
    @GET("tracks")
    suspend fun getTracks(
        @Header("Authorization") authorization: String,
        @Query("q") q: String,
        @Query("ids") ids: String?,
        @Query("genres") genres: String?,
        @Query("tags") tags: String?,
        @Query("bpm") bpm: SoundCloudBpm?,
        @Query("duration") duration: SoundCloudDuration?,
        @Query("created_at") createdAt: SoundCloudCreatedAt?,
        @Query("access") access: List<String>,
        @Query("limit") limit: Long,
        @Query("offset") offset: Long,
        @Query("linked_partitioning") linkedPartitioning: Boolean
    ): SoundCloudTrackResponse

    @GET("playlists")
    suspend fun getPlaylists(
        @Header("Authorization") authorization: String,
        @Query("q") q: String,
        @Query("access") access: List<String>,
        @Query("show_tracks") showTracks: Boolean?,
        @Query("limit") limit: Long,
        @Query("offset") offset: Long,
        @Query("linked_partitioning") linkedPartitioning: Boolean
    ): SoundCloudPlaylistResponse

    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") authorization: String,
        @Query("ids") ids: String?,
        @Query("limit") limit: Long,
        @Query("offset") offset: Long,
        @Query("linked_partitioning") linkedPartitioning: Boolean
    ): SoundCloudUserResponse

    @GET("tracks/{id}")
    suspend fun getTrackById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): SoundCloudTrackById

    @GET("playlists/{id}")
    suspend fun getPlaylistById(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): SoundCloudPlaylistById

    @GET("tracks/{id}/streams")
    suspend fun getSoundCloudTrackStreamableUrls(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): SoundCloudTrackStreamableUrls
}