package com.ile.syrin_x.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class UserCreatedPlaylistEntity(
    @PrimaryKey val userCreatedPlaylistId: String,
    val userId: String,
    val name: String,
    val dateAdded: LocalDate
)
