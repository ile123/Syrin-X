package com.ile.syrin_x.domain.repository

import com.ile.syrin_x.data.model.entity.FavoriteArtist
import com.ile.syrin_x.domain.core.Response
import kotlinx.coroutines.flow.Flow

interface FavoriteArtistRepository {
    suspend fun getAllUsersFavoriteArtists(userId: String): Flow<Response<List<FavoriteArtist>>>
    suspend fun getFavoriteArtistById(userId: String, artistId: String): Flow<Response<FavoriteArtist>>
    suspend fun addOrRemoveFavoriteArtist(artist: FavoriteArtist): Flow<Response<FavoriteArtist>>
}