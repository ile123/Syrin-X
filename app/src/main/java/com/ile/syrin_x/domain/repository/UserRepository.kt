package com.ile.syrin_x.domain.repository

import android.content.Context
import android.net.Uri
import com.ile.syrin_x.data.model.UserInfo
import com.ile.syrin_x.data.model.entity.FavoriteArtist
import com.ile.syrin_x.data.model.entity.NewReleaseNotificationEntity
import com.ile.syrin_x.domain.core.Response
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun saveUser(userId: String, userName: String, fullName: String, email: String, profileImageUrl: String?): Response<Void?>
    suspend fun updateUser(userId: String, userName: String, fullName: String): Flow<Response<UserInfo>>
    suspend fun getUserInfo(userId: String): Flow<Response<UserInfo>>
    suspend fun changeUserProfile(userId: String, profileImageUrl: String?): Response<UserInfo>
    suspend fun uploadProfileImageAndSet(userId: String, uri: Uri, context: Context): Response<UserInfo>
    suspend fun upgradeUserToPremium(userId: String)
    suspend fun removeUserPremiumPlan(userId: String)
    suspend fun getUserPremiumStatus(userId: String): Flow<Response<Boolean>>
    suspend fun getAllUsersNotifications(userId: String): Flow<Response<List<NewReleaseNotificationEntity>>>
    suspend fun markNotificationAsSeen(userId: String, notificationId: String)
}