package com.ile.syrin_x.domain.repository

import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.entity.PreviouslyPlayedTrack
import com.ile.syrin_x.domain.core.Response
import kotlinx.coroutines.flow.Flow

interface PreviouslyPlayedTrackRepository {
    suspend fun getAllPreviouslyPlayedTracksByUser(userId: String): Flow<Response<List<PreviouslyPlayedTrack>>>
    suspend fun getPreviouslyPlayedTrackById(trackId: String): Flow<Response<PreviouslyPlayedTrack>>
    suspend fun addPreviouslyPlayedTrack(track: UnifiedTrack, userId: String)
}