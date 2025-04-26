package com.ile.syrin_x.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.ile.syrin_x.data.enums.MusicSource
import java.time.LocalDate

@Entity(
    tableName = "playlist_tracks",
    foreignKeys = [
        ForeignKey(
            entity = UserCreatedPlaylistEntity::class,
            parentColumns = ["userCreatedPlaylistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserCreatedPlaylistTrackEntity(
    @PrimaryKey val userCreatedPlaylistTrackId: String,
    val playlistId: String,
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
    val timePlayed: Long,
    val dateCreated: LocalDate    // needs converter
)
