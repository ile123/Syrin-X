package com.ile.syrin_x.domain.usecase.musicsource.soundcloud

import com.ile.syrin_x.data.model.entity.SoundCloudUserToken
import com.ile.syrin_x.domain.repository.SoundCloudAuthRepository
import javax.inject.Inject

class GetSoundCloudUserTokenUseCase @Inject constructor(
    private val soundCloudRepository: SoundCloudAuthRepository
) {
    suspend operator fun invoke(userUuid: String): SoundCloudUserToken? = soundCloudRepository.getSoundCloudToken(userUuid)
}