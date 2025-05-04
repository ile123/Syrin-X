package com.ile.syrin_x.domain.usecase.musicsource.deezer

import com.ile.syrin_x.domain.repository.DeezerRepository
import javax.inject.Inject

class GetTrendingSongsByGenreUseCase @Inject constructor(
    private val deezerRepository: DeezerRepository
) {
    suspend operator fun invoke(genreId: Long, limit: Long, offset: Long) = deezerRepository.getAllTrendingSongsByGenre(genreId, limit, offset)
}