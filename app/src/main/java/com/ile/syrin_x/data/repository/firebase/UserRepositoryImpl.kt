package com.ile.syrin_x.data.repository.firebase

import com.google.firebase.database.FirebaseDatabase
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.UserRepository
import jakarta.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl @Inject constructor(
    private val db: FirebaseDatabase
) : UserRepository {
    override suspend fun saveUser(
        userId: String,
        userName: String,
        fullName: String,
        email: String
    ): Response<Void?> {
        return try {

            val newUser = mapOf(
                "userName" to userName,
                "fullName" to fullName,
                "email" to email
            )

            val usersRef = db
                .reference
                .child("users")
                .child(userId)

            suspendCoroutine<Unit> { continuation ->
                usersRef.setValue(newUser)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(Unit)
                        } else {
                            continuation.resumeWithException(Exception("Failed to save user data"))
                        }
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWithException(e)
                    }
            }

            Response.Success(null)
        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Oops, something went wrong.")
        }
    }
}
