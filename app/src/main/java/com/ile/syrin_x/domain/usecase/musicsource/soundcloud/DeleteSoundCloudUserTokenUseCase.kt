package com.ile.syrin_x.domain.usecase.musicsource.soundcloud

import com.ile.syrin_x.domain.repository.SoundCloudAuthRepository
import javax.inject.Inject

class DeleteSoundCloudUserTokenUseCase @Inject constructor(
    private val soundCloudAuthRepository: SoundCloudAuthRepository
) {
    suspend operator fun invoke(userUuid: String) = soundCloudAuthRepository.deleteSoundCloudToken(userUuid)
}