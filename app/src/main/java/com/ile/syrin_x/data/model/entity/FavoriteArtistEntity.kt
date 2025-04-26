package com.ile.syrin_x.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteArtistEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String
)
