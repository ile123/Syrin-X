package com.ile.syrin_x.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "new_releases")
data class NewReleaseNotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val artistId: Long,
    val title: String,
    val timestamp: Long,
    val seen: Boolean = false
)