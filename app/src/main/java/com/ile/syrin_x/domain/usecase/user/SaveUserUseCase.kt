package com.ile.syrin_x.domain.usecase.user

import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.UserRepository
import jakarta.inject.Inject

class SaveUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: String,
        userName: String,
        fullName: String,
        email: String,
        profileImageUrl: String?
    ): Response<Void?> {
        return userRepository.saveUser(userId, userName, fullName, email, profileImageUrl)
    }
}