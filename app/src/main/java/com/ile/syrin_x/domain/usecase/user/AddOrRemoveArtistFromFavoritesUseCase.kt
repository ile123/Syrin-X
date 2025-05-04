package com.ile.syrin_x.domain.usecase.user

import com.ile.syrin_x.data.model.entity.FavoriteArtist
import com.ile.syrin_x.domain.repository.FavoriteArtistRepository
import javax.inject.Inject

class AddOrRemoveArtistFromFavoritesUseCase @Inject constructor(
    private val favoriteArtistRepository: FavoriteArtistRepository
) {
    suspend operator fun invoke(artist: FavoriteArtist) = favoriteArtistRepository.addOrRemoveFavoriteArtist(artist)
}