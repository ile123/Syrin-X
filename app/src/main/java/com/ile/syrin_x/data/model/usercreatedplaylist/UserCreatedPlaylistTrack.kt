package com.ile.syrin_x.data.model.usercreatedplaylist

import androidx.room.PrimaryKey
import com.ile.syrin_x.data.enums.MusicSource
import java.time.LocalDate

data class UserCreatedPlaylistTrack(
    val userCreatedPlaylistTrackId: String,
    val trackId: String,
    val title: String?,
    val albumName: String?,
    val artists: List<String>?,
    val genre: String?,
    val durationMs: Int?,
    val explicit: Boolean?,
    val popularity: Int?,
    var playbackUrl: String?,
    val artworkUrl: String?,
    val musicSource: MusicSource,
    val timePlayed: Long
)