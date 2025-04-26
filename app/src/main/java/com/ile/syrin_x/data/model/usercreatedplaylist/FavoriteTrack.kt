package com.ile.syrin_x.data.model.usercreatedplaylist

import androidx.room.PrimaryKey
import com.ile.syrin_x.data.enums.MusicSource
import java.time.LocalDate

data class FavoriteTrack (
    val favoriteTrackId: String,
    val trackId: String,
    val userId: String,
    val name: String,
    val artist: String,
    val musicSource: MusicSource
)