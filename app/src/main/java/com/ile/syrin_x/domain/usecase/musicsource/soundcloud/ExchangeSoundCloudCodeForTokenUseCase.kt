package com.ile.syrin_x.domain.usecase.musicsource.soundcloud

import com.ile.syrin_x.data.model.entity.SoundCloudUserToken
import com.ile.syrin_x.domain.repository.SoundCloudAuthRepository
import javax.inject.Inject

class ExchangeSoundCloudCodeForTokenUseCase @Inject constructor(
    private val soundCloudRepository: SoundCloudAuthRepository
) {
    suspend operator fun invoke(userUuid: String, clientId: String, clientSecret: String, code: String, redirectUri: String): SoundCloudUserToken? = soundCloudRepository.getToken(userUuid, clientId, clientSecret, code, redirectUri)
}