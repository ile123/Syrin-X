package com.ile.syrin_x.domain.usecase.musicsource.spotify

import com.ile.syrin_x.domain.repository.SpotifyRepository
import javax.inject.Inject

class GetTrackByIdSpotifyUseCase  @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    suspend operator fun invoke(id: String, accessToken: String) = spotifyRepository.searchTrackById(id, accessToken)
}