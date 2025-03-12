package com.ile.syrin_x.domain.usecase.musicsource.spotify

import com.ile.syrin_x.domain.repository.SpotifyAuthRepository
import jakarta.inject.Inject

class GetSpotifyTokenFromDbUseCase @Inject constructor(
    private val spotifyAuthRepository: SpotifyAuthRepository
) {
    suspend operator fun invoke(userUuid: String) = spotifyAuthRepository.getSpotifyToken(userUuid)
}