package com.ile.syrin_x.domain.usecase.user

import com.ile.syrin_x.domain.repository.UserRepository
import jakarta.inject.Inject

class RemovePremiumFromUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: String
    ) = userRepository.removeUserPremiumPlan(userId)
}