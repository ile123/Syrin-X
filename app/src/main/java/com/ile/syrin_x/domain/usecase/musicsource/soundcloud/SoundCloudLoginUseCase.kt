package com.ile.syrin_x.domain.usecase.musicsource.soundcloud

import com.ile.syrin_x.domain.repository.SoundCloudRepository
import javax.inject.Inject

class SoundCloudLoginUseCase @Inject constructor(
    private val soundCloudRepository: SoundCloudRepository
) {
    suspend operator fun invoke(userUuid: String, clientId: String, clientSecret: String, code: String, redirectUri: String) = soundCloudRepository.getToken(userUuid, clientId, clientSecret, code, redirectUri)
}