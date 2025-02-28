package com.ile.syrin_x.domain.usecase.auth

import com.ile.syrin_x.domain.repository.AuthenticationRepository
import jakarta.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String) = authenticationRepository.resetPassword(email)
}