package com.ile.syrin_x.domain.usecase.user

import com.ile.syrin_x.data.model.UserInfo
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.UserRepository
import jakarta.inject.Inject

class ChangeUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: String,
        profileImage: String?
    ): Response<UserInfo> {
        return userRepository.changeUserProfile(userId, profileImage)
    }
}