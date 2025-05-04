package com.ile.syrin_x.domain.usecase.musicsource.deezer

import com.ile.syrin_x.domain.repository.DeezerRepository
import javax.inject.Inject

class GetAllMusicGenresUseCase @Inject constructor(
    private val deezerRepository: DeezerRepository
) {
    suspend operator fun invoke() = deezerRepository.getAllGenres()
}