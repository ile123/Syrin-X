package com.ile.syrin_x.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import com.ile.syrin_x.data.enums.MusicSource
import java.time.LocalDate

@IgnoreExtraProperties
@Entity
data class FavoriteTrackEntity(
    @PrimaryKey var favoriteTrackId: String = "",
    var trackId: String = "",
    var userId: String = "",
    var name: String = "",
    var artist: String = "",
    var musicSource: MusicSource = MusicSource.NOT_SPECIFIED
)