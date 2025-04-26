package com.ile.syrin_x.domain.usecase.usercreatedplaylist

import com.ile.syrin_x.domain.repository.FavoriteTrackRepository
import jakarta.inject.Inject

class GetAllFavoriteTracksByUserUseCase @Inject constructor(
    private val favoriteTrackRepository: FavoriteTrackRepository
) {
    suspend operator fun invoke(userId: String) =
        favoriteTrackRepository.getAllFavoriteTracksByUser(userId)
}