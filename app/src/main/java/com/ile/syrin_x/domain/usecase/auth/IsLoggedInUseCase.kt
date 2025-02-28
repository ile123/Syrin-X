package com.ile.syrin_x.domain.usecase.auth

import com.ile.syrin_x.domain.repository.AuthenticationRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.flow

class IsLoggedInUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    operator fun invoke() = flow { emit(authenticationRepository.isLoggedIn()) }
}