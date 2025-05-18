package com.ile.syrin_x.domain.usecase.user

import com.ile.syrin_x.data.model.entity.NewReleaseNotificationEntity
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.repository.UserRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class MarkUserNotificationAsSeenUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userId: String,
        notificationId: String
    ) = userRepository.markNotificationAsSeen(userId, notificationId)
}