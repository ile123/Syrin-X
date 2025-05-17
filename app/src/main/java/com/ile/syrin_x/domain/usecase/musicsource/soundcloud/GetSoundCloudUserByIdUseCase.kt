package com.ile.syrin_x.domain.usecase.musicsource.soundcloud

import com.ile.syrin_x.domain.repository.SoundCloudRepository
import javax.inject.Inject

class GetSoundCloudUserByIdUseCase @Inject constructor(
    private val soundCloudRepository: SoundCloudRepository
) {
    suspend operator fun invoke(userId: String, accessToken: String) = soundCloudRepository.searchUserById(userId, accessToken)
}