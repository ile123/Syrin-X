package com.ile.syrin_x.domain.usecase.musicsource.spotify

import com.ile.syrin_x.domain.repository.SpotifyRepository
import javax.inject.Inject

class SearchAllSpotifyUseCase  @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    suspend operator fun invoke(keyword: String, accessToken: String) = spotifyRepository.searchAll(keyword, accessToken)
}