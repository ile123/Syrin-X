package com.ile.syrin_x.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "new_releases",
    primaryKeys = ["userId","trackId"]
)
data class NewReleaseNotificationEntity(
    val userId: String,
    val trackId: Long,
    val artistId: Long,
    val title: String,
    val timestamp: Long,
    val seen: Boolean = false
)