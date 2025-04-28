package com.ile.syrin_x.domain.repository

import com.ile.syrin_x.data.model.entity.FavoriteArtist
import com.ile.syrin_x.domain.core.Response
import kotlinx.coroutines.flow.Flow

interface FavoriteArtistRepository {
    suspend fun getAllFavoriteArtistsByUser(userId: String): Flow<Response<List<FavoriteArtist>>>
    suspend fun getFavoriteArtistById(artistId: String): Flow<Response<FavoriteArtist>>
    suspend fun addFavoriteArtist(artist: FavoriteArtist): Flow<Response<FavoriteArtist>>
}