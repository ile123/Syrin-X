package com.ile.syrin_x.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ile.syrin_x.data.enums.MusicSource

@Entity
data class PreviouslyPlayedTrackEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String?,
    val albumName: String?,
    val artists: List<String>?,
    val genre: String?,
    val durationMs: Int?,
    val explicit: Boolean?,
    val popularity: Int?,
    var playbackUrl: String?,
    val artworkUrl: String?,
    val musicSource: MusicSource
)
