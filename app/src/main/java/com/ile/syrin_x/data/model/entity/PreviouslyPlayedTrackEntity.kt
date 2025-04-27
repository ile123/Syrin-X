package com.ile.syrin_x.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import com.ile.syrin_x.data.enums.MusicSource

@IgnoreExtraProperties
@Entity
data class PreviouslyPlayedTrackEntity(
    @PrimaryKey var id: String = "",
    var userId: String = "",
    var title: String? = null,
    var albumName: String? = null,
    var artists: List<String>? = null,
    var genre: String? = null,
    var durationMs: Int? = null,
    var explicit: Boolean? = null,
    var popularity: Int? = null,
    var playbackUrl: String? = null,
    var artworkUrl: String? = null,
    var musicSource: MusicSource = MusicSource.SPOTIFY
)
