package com.ile.syrin_x.domain.usecase.usercreatedplaylist

import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.domain.repository.FavoriteTrackRepository
import jakarta.inject.Inject

class AddOrRemoveTrackFromFavoritesUseCase @Inject constructor(
    private val favoriteTrackRepository: FavoriteTrackRepository
) {
    suspend operator fun invoke(track: UnifiedTrack, userId: String) =
        favoriteTrackRepository.addOrRemoveTrackFromFavorites(track, userId)
}