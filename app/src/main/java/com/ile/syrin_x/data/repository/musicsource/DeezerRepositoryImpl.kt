package com.ile.syrin_x.data.repository.musicsource

import com.ile.syrin_x.data.api.DeezerApi
import com.ile.syrin_x.data.model.deezer.MusicGenre
import com.ile.syrin_x.data.model.deezer.TrackByGenre
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.DeezerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeezerRepositoryImpl @Inject constructor(
    private val api: DeezerApi
): DeezerRepository {
    override suspend fun getAllGenres(): Flow<Response<List<MusicGenre>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSongsByGenre(): Flow<Response<List<TrackByGenre>>> {
        TODO("Not yet implemented")
    }
}