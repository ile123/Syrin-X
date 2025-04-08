package com.ile.syrin_x.domain.usecase.musicsource.soundcloud

import com.ile.syrin_x.domain.repository.SoundCloudRepository
import com.ile.syrin_x.domain.repository.SpotifyRepository
import javax.inject.Inject

class GetTrackByIdSoundCloudUseCase  @Inject constructor(
    private val soundCloudRepository: SoundCloudRepository
) {
    suspend operator fun invoke(id: String, accessToken: String) = soundCloudRepository.searchTrackById(id, accessToken)
}