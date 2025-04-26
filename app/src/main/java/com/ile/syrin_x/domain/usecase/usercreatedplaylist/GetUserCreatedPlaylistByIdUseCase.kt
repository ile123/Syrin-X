package com.ile.syrin_x.domain.usecase.usercreatedplaylist

import com.ile.syrin_x.domain.repository.UserCreatedPlaylistRepository
import jakarta.inject.Inject

class GetUserCreatedPlaylistByIdUseCase @Inject constructor(
    private val userCreatedPlaylistRepository: UserCreatedPlaylistRepository
) {
    suspend operator fun invoke(playlistId: String) =
        userCreatedPlaylistRepository.getUserCreatedPlaylistById(playlistId)
}