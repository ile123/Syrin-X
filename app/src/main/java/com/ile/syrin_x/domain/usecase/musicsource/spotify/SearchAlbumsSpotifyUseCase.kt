package com.ile.syrin_x.domain.usecase.musicsource.spotify

import com.ile.syrin_x.domain.repository.SpotifyRepository
import javax.inject.Inject

class SearchAlbumsSpotifyUseCase  @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    suspend operator fun invoke(keyword: String, limit: Long, offset: Long, accessToken: String) = spotifyRepository.searchAlbums(keyword, limit, offset, accessToken)
}