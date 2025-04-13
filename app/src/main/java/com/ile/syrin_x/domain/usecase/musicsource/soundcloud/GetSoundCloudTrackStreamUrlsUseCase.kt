package com.ile.syrin_x.domain.usecase.musicsource.soundcloud

import com.ile.syrin_x.domain.repository.SoundCloudRepository
import javax.inject.Inject

class GetSoundCloudTrackStreamUrlsUseCase @Inject constructor(
    private val soundCloudRepository: SoundCloudRepository
) {
    suspend operator fun invoke(id: String, accessToken: String) = soundCloudRepository.getSoundCloudTrackStreamUrls(id, accessToken)
}