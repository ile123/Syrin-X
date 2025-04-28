package com.ile.syrin_x.domain.usecase.usercreatedplaylist

import com.ile.syrin_x.domain.repository.FavoriteTrackRepository
import jakarta.inject.Inject

class GetFavoriteTrackByIdUseCase @Inject constructor(
    private val favoriteTrackRepository: FavoriteTrackRepository
) {
    suspend operator fun invoke(favoriteTrackId: String) =
        favoriteTrackRepository.getFavoriteTrackById(favoriteTrackId)
}