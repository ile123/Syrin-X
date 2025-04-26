package com.ile.syrin_x.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ile.syrin_x.data.enums.MusicSource
import java.time.LocalDate

@Entity
data class FavoriteTrackEntity (
    @PrimaryKey val favoriteTrackId: String,
    val trackId: String,
    val userId: String,
    val name: String,
    val artist: String,
    val musicSource: MusicSource,
    val dateCreated: LocalDate
)