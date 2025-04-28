package com.ile.syrin_x.domain.repository

import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.usercreatedplaylist.FavoriteTrack
import com.ile.syrin_x.domain.core.Response
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackRepository {
    suspend fun getAllFavoriteTracksByUser(userId: String): Flow<Response<List<FavoriteTrack>>>
    suspend fun getFavoriteTrackById(favoriteTrackId: String): Flow<Response<FavoriteTrack>>
    suspend fun addOrRemoveTrackFromFavorites(track: UnifiedTrack, userId: String): Flow<Response<FavoriteTrack>>
}