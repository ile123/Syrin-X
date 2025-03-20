package com.ile.syrin_x.domain.usecase.musicsource.spotify

import com.ile.syrin_x.domain.repository.SpotifyAuthRepository
import javax.inject.Inject

class RefreshSpotifyAccessToken @Inject constructor(
    private val spotifyAuthRepository: SpotifyAuthRepository
) {
    suspend operator fun invoke(userUuid: String, refreshToken: String?) = spotifyAuthRepository.refreshSpotifyAccessToken(userUuid, refreshToken)
}