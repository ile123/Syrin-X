package com.ile.syrin_x.domain.usecase.user

import android.content.Context
import android.net.Uri
import com.ile.syrin_x.data.model.UserInfo
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.UserRepository
import javax.inject.Inject

class UploadProfileImageUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: String,
        uri: Uri,
        context: Context
    ): Response<UserInfo> {
        return userRepository.uploadProfileImageAndSet(userId, uri, context)
    }
}