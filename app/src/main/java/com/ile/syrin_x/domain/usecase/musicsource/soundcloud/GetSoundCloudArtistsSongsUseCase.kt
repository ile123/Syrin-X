package com.ile.syrin_x.domain.usecase.musicsource.soundcloud

import com.ile.syrin_x.domain.repository.SoundCloudRepository
import javax.inject.Inject

class GetSoundCloudArtistsSongsUseCase @Inject constructor(
    private val soundCloudRepository: SoundCloudRepository
) {
    suspend operator fun invoke(id: String, offset: Long, limit:Long, accessToken: String) = soundCloudRepository.searchSongsByUser(id, offset, limit, accessToken)
}