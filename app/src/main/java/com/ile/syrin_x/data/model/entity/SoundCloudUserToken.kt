package com.ile.syrin_x.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SoundCloudUserToken(
    @PrimaryKey val userId: String,
    val accessToken: String,
    val refreshToken: String?,
    val expiresAt: Long
)

