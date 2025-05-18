package com.ile.syrin_x.domain.usecase.user

import com.ile.syrin_x.domain.repository.FavoriteArtistRepository
import javax.inject.Inject

class GetAllFavoriteArtistsByUserUseCase @Inject constructor(
    private val favoriteArtistsRepository: FavoriteArtistRepository
) {
    suspend operator fun invoke(userId: String) = favoriteArtistsRepository.getAllUsersFavoriteArtists(userId)
}