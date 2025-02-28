package com.ile.syrin_x.domain.usecase.auth

import com.ile.syrin_x.domain.repository.AuthenticationRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.flow

class GetUserUidUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke() = flow { emit(authenticationRepository.userUid()) }
}