package com.ile.syrin_x.domain.usecase.musicsource.spotify

import com.ile.syrin_x.domain.repository.SpotifyAuthRepository
import javax.inject.Inject

class SpotifyLoginUseCase @Inject constructor(
    private val spotifyAuthRepository: SpotifyAuthRepository
) {
    suspend operator fun invoke(userUuid: String, authorization: String, code: String, redirectUri: String) = spotifyAuthRepository.getToken(userUuid, authorization, code, redirectUri)
}