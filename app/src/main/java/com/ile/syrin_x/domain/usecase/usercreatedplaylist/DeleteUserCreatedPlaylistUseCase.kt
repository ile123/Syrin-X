package com.ile.syrin_x.domain.usecase.usercreatedplaylist

import com.ile.syrin_x.data.enums.UserCreatedPlaylistTrackAction
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.domain.repository.UserCreatedPlaylistRepository
import jakarta.inject.Inject

class DeleteUserCreatedPlaylistUseCase @Inject constructor(
    private val userCreatedPlaylistRepository: UserCreatedPlaylistRepository
) {
    suspend operator fun invoke(playlistId: String) =
        userCreatedPlaylistRepository.deleteUserCreatedPlaylist(playlistId)
}