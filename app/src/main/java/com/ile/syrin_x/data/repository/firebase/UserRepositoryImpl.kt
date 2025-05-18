package com.ile.syrin_x.data.repository.firebase

import android.content.Context
import android.net.Uri
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.ile.syrin_x.data.database.FavoriteArtistDao
import com.ile.syrin_x.data.database.NewReleaseNotificationDao
import com.ile.syrin_x.data.model.UserInfo
import com.ile.syrin_x.data.model.entity.FavoriteArtist
import com.ile.syrin_x.data.model.entity.NewReleaseNotificationEntity
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.UserRepository
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepositoryImpl @Inject constructor(
    private val db: FirebaseDatabase,
    private val storage: FirebaseStorage,
    private val newReleaseNotificationDao: NewReleaseNotificationDao
) : UserRepository {

    override suspend fun saveUser(
        userId: String,
        userName: String,
        fullName: String,
        email: String,
        profileImageUrl: String?
    ): Response<Void?> {
        return try {
            val newUser = mutableMapOf<String, Any>(
                "userName" to userName,
                "fullName" to fullName,
                "email" to email,
                "premium" to false
            )

            profileImageUrl?.takeIf { it.isNotBlank() }?.let {
                newUser["profilePicture"] = it
            }

            val usersRef = db.reference.child("users").child(userId)

            suspendCoroutine { continuation ->
                usersRef.setValue(newUser)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) continuation.resume(Unit)
                        else continuation.resumeWithException(Exception("Failed to save user data"))
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

    override suspend fun updateUser(userId: String, userName: String, fullName: String): Flow<Response<UserInfo>> = flow {
        emit(Response.Loading)

        val updateMap = mapOf(
            "userName" to userName,
            "fullName" to fullName
        )

        val usersRef = db.reference.child("users").child(userId)

        suspendCoroutine { continuation ->
            usersRef.updateChildren(updateMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) continuation.resume(Unit)
                    else continuation.resumeWithException(Exception("Failed to update user"))
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }

        emitAll(
            getUserInfo(userId)
                .filterNot { it is Response.Loading }
        )

    }.catch { e ->
        emit(Response.Error(e.localizedMessage ?: "Failed to update user"))
    }
    override suspend fun getUserInfo(userId: String): Flow<Response<UserInfo>> = flow {
        emit(Response.Loading)

        val userRef = db.reference.child("users").child(userId)

        val snapshot = suspendCancellableCoroutine<DataSnapshot> { continuation ->
            userRef.get()
                .addOnSuccessListener { continuation.resume(it) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }

        val data = snapshot.value as? Map<*, *>
        if (data != null) {
            val userInfo = parseUserInfo(data)
            emit(Response.Success(userInfo))
        } else {
            emit(Response.Error("User not found"))
        }

    }.catch { e ->
        emit(Response.Error(e.localizedMessage ?: "Failed to fetch user"))
    }

    override suspend fun changeUserProfile(userId: String, profileImageUrl: String?): Response<UserInfo> {
        return try {
            if (profileImageUrl.isNullOrBlank()) {
                return Response.Error("Profile image URL is empty")
            }

            val updateMap = mapOf("profilePicture" to profileImageUrl)
            val userRef = db.reference.child("users").child(userId)

            suspendCoroutine { continuation ->
                userRef.updateChildren(updateMap)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) continuation.resume(Unit)
                        else continuation.resumeWithException(Exception("Failed to update profile image"))
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWithException(e)
                    }
            }

            getUserInfo(userId)
                .filterNot { it is Response.Loading }
                .first()

        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to update profile image")
        }
    }

    override suspend fun uploadProfileImageAndSet(
        userId: String,
        uri: Uri,
        context: Context
    ): Response<UserInfo> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Response.Error("Unable to open image")

            val ref = storage.reference.child("profile_images/$userId.jpg")
            ref.putStream(inputStream).await()
            val downloadUrl = ref.downloadUrl.await().toString()

            return changeUserProfile(userId, downloadUrl)

        } catch (e: Exception) {
            Response.Error(e.localizedMessage ?: "Failed to upload image")
        }
    }

    override suspend fun upgradeUserToPremium(userId: String) {
        val updateMap = mapOf(
            "premium" to true,
        )

        val usersRef = db.reference.child("users").child(userId)

        suspendCoroutine { continuation ->
            usersRef.updateChildren(updateMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) continuation.resume(Unit)
                    else continuation.resumeWithException(Exception("Failed to update user"))
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    override suspend fun getUserPremiumStatus(userId: String): Flow<Response<Boolean>> = flow {
        emit(Response.Loading)

        val userRef = db.reference.child("users").child(userId)

        val snapshot = suspendCancellableCoroutine<DataSnapshot> { continuation ->
            userRef.get()
                .addOnSuccessListener { continuation.resume(it) }
                .addOnFailureListener { e -> continuation.resumeWithException(e) }
        }

        val data = snapshot.value as? Map<*, *>
        if (data != null) {
            val isPremium = data["premium"] as Boolean
            emit(Response.Success(isPremium))
        } else {
            emit(Response.Error("User not found"))
        }



    }.catch { e ->
        emit(Response.Error(e.localizedMessage ?: "Failed to fetch user"))
    }

    override suspend fun getAllUsersNotifications(
        userId: String
    ): Flow<Response<List<NewReleaseNotificationEntity>>> = flow {
        emit(Response.Loading)
        val usersNotifications = withContext(Dispatchers.IO) {
            newReleaseNotificationDao.getAllForUser(userId)
        }
        emit(Response.Success(usersNotifications))
    }

    override suspend fun markNotificationAsSeen(userId: String, notificationId: String) {
        newReleaseNotificationDao.markSeen(notificationId)
        db
            .getReference("users/$userId/newReleasesNotifications/$notificationId/seen")
            .setValue(true)
    }

    private fun parseUserInfo(data: Map<*, *>): UserInfo {
        return UserInfo(
            userName = data["userName"] as? String ?: "",
            fullName = data["fullName"] as? String ?: "",
            email = data["email"] as? String ?: "",
            profilePicture = data["profilePicture"] as? String
        )
    }
}
