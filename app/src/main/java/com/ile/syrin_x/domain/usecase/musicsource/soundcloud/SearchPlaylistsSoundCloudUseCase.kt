package com.ile.syrin_x.domain.usecase.musicsource.soundcloud

import com.ile.syrin_x.domain.repository.SoundCloudRepository
import javax.inject.Inject

class SearchPlaylistsSoundCloudUseCase @Inject constructor(
    private val soundCloudRepository: SoundCloudRepository
) {
    suspend operator fun invoke(keyword: String, limit: Long, offset: Long, accessToken: String) = soundCloudRepository.searchPlaylists(keyword, limit, offset, accessToken)
}