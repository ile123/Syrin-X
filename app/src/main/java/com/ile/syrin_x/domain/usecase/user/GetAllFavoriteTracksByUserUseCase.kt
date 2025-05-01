package com.ile.syrin_x.domain.usecase.user

import com.ile.syrin_x.domain.repository.FavoriteTrackRepository
import jakarta.inject.Inject

class GetAllFavoriteTracksByUserUseCase @Inject constructor(
    private val favoriteTrackRepository: FavoriteTrackRepository
) {
    suspend operator fun invoke(userId: String) =
        favoriteTrackRepository.getAllFavoriteTracksByUser(userId)
}