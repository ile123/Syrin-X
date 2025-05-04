package com.ile.syrin_x.domain.repository

import com.ile.syrin_x.data.model.deezer.MusicGenre
import com.ile.syrin_x.data.model.deezer.TrackByGenre
import com.ile.syrin_x.domain.core.Response
import kotlinx.coroutines.flow.Flow

interface DeezerRepository {
    suspend fun getAllGenres(): Flow<Response<List<MusicGenre>>>
    suspend fun getAllSongsByGenre(): Flow<Response<List<TrackByGenre>>>
}