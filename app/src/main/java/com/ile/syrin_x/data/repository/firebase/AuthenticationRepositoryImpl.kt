package com.ile.syrin_x.data.repository.firebase

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.AuthenticationRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class AuthenticationRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthenticationRepository {

    override suspend fun userUid(): String = auth.currentUser?.uid ?: ""

    override suspend fun isLoggedIn(): Boolean = auth.currentUser == null

    override suspend fun logout() = auth.signOut()

    override suspend fun login(email: String, password: String): Flow<Response<AuthResult>> = flow {
        try {
            emit(Response.Loading)
            val data = auth.signInWithEmailAndPassword(email, password).await()
            emit(Response.Success(data))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Oops, something went wrong."))
        }
    }

    override suspend fun register(email: String, password: String): Flow<Response<AuthResult>> =
        flow {
            try {
                emit(Response.Loading)
                val data = auth.createUserWithEmailAndPassword(email, password).await()
                emit(Response.Success(data))
            } catch (e: Exception) {
                emit(Response.Error(e.localizedMessage ?: "Oops, something went wrong."))
            }
        }

    override suspend fun resetPassword(email: String): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val data = auth.sendPasswordResetEmail(email).await()
            emit(Response.Success(data))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Oops, something went wrong."))
        }
    }

}