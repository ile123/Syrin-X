package com.ile.syrin_x.domain.usecase.usercreatedplaylist

import com.ile.syrin_x.data.enums.UserCreatedPlaylistTrackAction
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.domain.repository.UserCreatedPlaylistRepository
import jakarta.inject.Inject

class AddOrRemoveUserCreatedPlaylistTrackUseCase @Inject constructor(
    private val userCreatedPlaylistRepository: UserCreatedPlaylistRepository
) {
    suspend operator fun invoke(track: UnifiedTrack, playlistId: String, action: UserCreatedPlaylistTrackAction) =
        userCreatedPlaylistRepository.addOrRemoveUserCreatedPlaylistTrack(track, playlistId, action)
}