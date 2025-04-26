package com.ile.syrin_x.domain.usecase.usercreatedplaylist

import com.ile.syrin_x.domain.repository.UserCreatedPlaylistRepository
import com.ile.syrin_x.domain.repository.UserRepository
import jakarta.inject.Inject

class GetAllUserCreatedPlaylistsByUserUseCase @Inject constructor(
    private val userCreatedPlaylistRepository: UserCreatedPlaylistRepository
) {
    suspend operator fun invoke(userId: String) =
        userCreatedPlaylistRepository.getAllUserCreatedPlaylistsByUser(userId)
}