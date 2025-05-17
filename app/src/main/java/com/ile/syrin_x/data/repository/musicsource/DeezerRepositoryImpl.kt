package com.ile.syrin_x.data.repository.musicsource

import android.util.Log
import com.ile.syrin_x.data.api.DeezerApi
import com.ile.syrin_x.data.model.deezer.MusicGenre
import com.ile.syrin_x.data.model.deezer.TrackByGenre
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.DeezerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeezerRepositoryImpl @Inject constructor(
    private val api: DeezerApi
): DeezerRepository {
    override suspend fun getAllGenres(): Flow<Response<List<MusicGenre>>> = flow {
        try {
            emit(Response.Loading)
            val data = api.getGenres("en")
            emit(Response.Success(data.data))
        } catch (e: Exception) {
            Log.d("DeezerRepository Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }

    override suspend fun getAllTrendingSongsByGenre(genreId: Long, limit: Long, offset: Long): Flow<Response<List<TrackByGenre>>> = flow {
        try {
            emit(Response.Loading)
            val data = api.getTrendingTracksByGenre(genreId,"en", limit, offset)
            emit(Response.Success(data.data))
        } catch (e: Exception) {
            Log.d("DeezerRepository Error", e.message.toString())
            emit(Response.Error(e.message.toString()))
        }
    }
}