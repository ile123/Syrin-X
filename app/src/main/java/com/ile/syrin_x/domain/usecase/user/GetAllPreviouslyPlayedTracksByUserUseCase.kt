package com.ile.syrin_x.domain.usecase.user

import com.ile.syrin_x.domain.repository.PreviouslyPlayedTrackRepository
import javax.inject.Inject

class GetAllPreviouslyPlayedTracksByUserUseCase @Inject constructor(
    private val previouslyPlayedTrackRepository: PreviouslyPlayedTrackRepository
) {
    suspend operator fun invoke(userId: String) = previouslyPlayedTrackRepository.getAllPreviouslyPlayedTracksByUser(userId)
}