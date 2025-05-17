package com.ile.syrin_x.domain.usecase.musicsource.spotify

import com.ile.syrin_x.domain.repository.SpotifyRepository
import jakarta.inject.Inject

class GetSpotifyArtistByIdUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    suspend operator fun invoke(artistId: String, accessToken: String) = spotifyRepository.searchArtistById(artistId, accessToken)
}