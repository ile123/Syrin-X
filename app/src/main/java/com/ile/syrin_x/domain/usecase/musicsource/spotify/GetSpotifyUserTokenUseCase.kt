package com.ile.syrin_x.domain.usecase.musicsource.spotify

import com.ile.syrin_x.data.model.SpotifyUserToken
import com.ile.syrin_x.domain.repository.SpotifyAuthRepository
import jakarta.inject.Inject

class GetSpotifyUserTokenUseCase @Inject constructor(
    private val spotifyAuthRepository: SpotifyAuthRepository
) {
    suspend operator fun invoke(userUuid: String): SpotifyUserToken? = spotifyAuthRepository.getSpotifyToken(userUuid)
}