package com.ile.syrin_x.domain.usecase.usercreatedplaylist

import com.ile.syrin_x.domain.repository.UserCreatedPlaylistRepository
import jakarta.inject.Inject

class CreateUserCreatedPlaylistUseCase @Inject constructor(
    private val userCreatedPlaylistRepository: UserCreatedPlaylistRepository
) {
    suspend operator fun invoke(name: String, userId: String) =
        userCreatedPlaylistRepository.createUserCreatedPlaylist(name, userId)
}