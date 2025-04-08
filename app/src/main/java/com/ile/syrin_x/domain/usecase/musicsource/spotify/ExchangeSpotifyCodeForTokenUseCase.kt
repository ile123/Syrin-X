package com.ile.syrin_x.domain.usecase.musicsource.spotify

import com.ile.syrin_x.data.model.spotify.SpotifyUserToken
import com.ile.syrin_x.domain.repository.SpotifyAuthRepository
import javax.inject.Inject

class ExchangeSpotifyCodeForTokenUseCase @Inject constructor(
    private val spotifyAuthRepository: SpotifyAuthRepository
) {
    suspend operator fun invoke(userUuid: String, authorization: String, code: String, redirectUri: String): SpotifyUserToken? = spotifyAuthRepository.getToken(userUuid, authorization, code, redirectUri)
}