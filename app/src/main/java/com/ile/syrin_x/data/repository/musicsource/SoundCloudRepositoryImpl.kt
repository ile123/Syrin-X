package com.ile.syrin_x.data.repository.musicsource

import android.util.Log
import com.ile.syrin_x.data.api.SoundCloudApi
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.SoundCloudRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SoundCloudRepositoryImpl @Inject constructor(
    private val api: SoundCloudApi
): SoundCloudRepository {
    override suspend fun searchTracks(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<Any>> = flow {
        try {
            emit(Response.Loading)
            val authorization = "OAuth $accessToken"
            val access = listOf("playable", "preview", "blocked")
            val result = api.getTracks(
                authorization,
                keyword,
                null,
                null,
                null,
                null,
                null,
                null,
                access,
                limit,
                offset,
                true
                )
            emit(Response.Success(result))
        } catch (e: Exception) {
            Log.d("SoundCloud Search Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun searchUsers(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<Any>> = flow {
        try {
            emit(Response.Loading)
            val authorization = "OAuth $accessToken"
            val result = api.getUsers(authorization, null, limit, offset, true)
            emit(Response.Success(result))
        } catch (e: Exception) {
            Log.d("SoundCloud Search Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun searchPlaylists(keyword: String, limit: Long, offset: Long, accessToken: String): Flow<Response<Any>> = flow {
        try {
            emit(Response.Loading)
            val authorization = "OAuth $accessToken"
            val access = listOf("playable", "preview", "blocked")
            val result = api.getPlaylists(authorization, keyword, access, true, limit, offset, true)
            emit(Response.Success(result))
        } catch (e: Exception) {
            Log.d("SoundCloud Search Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }
}