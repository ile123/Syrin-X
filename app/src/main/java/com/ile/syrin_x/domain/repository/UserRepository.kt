package com.ile.syrin_x.domain.repository

import com.ile.syrin_x.domain.core.Response
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun saveUser(userId: String, userName: String, fullName: String, email: String): Response<Void?>
}