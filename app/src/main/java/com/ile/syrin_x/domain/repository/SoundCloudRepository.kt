package com.ile.syrin_x.domain.repository

import com.ile.syrin_x.data.model.soundcloud.SoundCloudTrackStreamableUrls
import com.ile.syrin_x.domain.core.Response
import kotlinx.coroutines.flow.Flow

interface SoundCloudRepository {
    suspend fun searchTracks(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<Any>>
    suspend fun searchUsers(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<Any>>
    suspend fun searchPlaylists(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<Any>>
    suspend fun searchTrackById(id: String, accessToken: String): Flow<Response<Any>>
    suspend fun searchPlaylistById(id: String, accessToken: String): Flow<Response<Any>>
    suspend fun searchUserById(id: String, accessToken: String): Flow<Response<Any>>
    suspend fun searchSongsByUser(userId: String, offset: Long, limit: Long, accessToken: String): Flow<Response<Any>>
    suspend fun getSoundCloudTrackStreamUrls(id: String, accessToken: String): SoundCloudTrackStreamableUrls
}