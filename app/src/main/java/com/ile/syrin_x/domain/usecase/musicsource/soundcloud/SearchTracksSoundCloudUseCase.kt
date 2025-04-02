package com.ile.syrin_x.domain.usecase.musicsource.soundcloud

import com.ile.syrin_x.domain.repository.SoundCloudRepository
import javax.inject.Inject

class SearchTracksSoundCloudUseCase @Inject constructor(
    private val  soundCloudRepository: SoundCloudRepository
) {
    suspend operator fun invoke(keyword: String, limit: Long, offset: Long, accessToken: String) = soundCloudRepository.searchTracks(keyword, limit, offset, accessToken)
}