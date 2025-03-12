package com.ile.syrin_x.domain.usecase.auth

import com.ile.syrin_x.domain.repository.AuthenticationRepository
import jakarta.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        authenticationRepository.register(email, password)
}