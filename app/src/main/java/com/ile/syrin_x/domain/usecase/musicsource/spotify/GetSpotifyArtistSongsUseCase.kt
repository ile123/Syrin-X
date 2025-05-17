package com.ile.syrin_x.domain.usecase.musicsource.spotify

import com.ile.syrin_x.data.model.entity.SpotifyUserToken
import com.ile.syrin_x.domain.repository.SpotifyAuthRepository
import com.ile.syrin_x.domain.repository.SpotifyRepository
import jakarta.inject.Inject

class GetSpotifyArtistSongsUseCase @Inject constructor(
    private val spotifyRepository: SpotifyRepository
) {
    suspend operator fun invoke(artistId: String, offset: Long, limit: Long, accessToken: String) = spotifyRepository.getAllSongsByArtist(artistId, offset, limit, accessToken)
}