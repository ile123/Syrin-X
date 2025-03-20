package com.ile.syrin_x.domain.usecase.musicsource.soundcloud

import com.ile.syrin_x.domain.repository.SoundCloudAuthRepository
import javax.inject.Inject

class RefreshSoundcloudAccessToken @Inject constructor(
    private val soundCloudAuthRepository: SoundCloudAuthRepository
) {
    suspend operator fun invoke(userUuid: String, refreshToken: String?) = soundCloudAuthRepository.refreshSoundCloudAccessToken(userUuid, refreshToken)
}