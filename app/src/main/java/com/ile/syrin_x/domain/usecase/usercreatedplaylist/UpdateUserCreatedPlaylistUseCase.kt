package com.ile.syrin_x.domain.usecase.usercreatedplaylist

import com.ile.syrin_x.domain.repository.UserCreatedPlaylistRepository
import jakarta.inject.Inject

class UpdateUserCreatedPlaylistUseCase @Inject constructor(
    private val userCreatedPlaylistRepository: UserCreatedPlaylistRepository
) {
    suspend operator fun invoke(newName: String, playlistId: String) =
        userCreatedPlaylistRepository.updateUserCreatedPlaylist(newName, playlistId)
}