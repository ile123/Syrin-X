package com.ile.syrin_x.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import com.ile.syrin_x.data.enums.MusicSource
import java.time.LocalDate

@IgnoreExtraProperties
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
    @PrimaryKey var userCreatedPlaylistTrackId: String = "",
    var playlistId: String = "",
    var trackId: String = "",
    var title: String? = null,
    var albumName: String? = null,
    var artists: List<String>? = null,
    var genre: String? = null,
    var durationMs: Int? = null,
    var explicit: Boolean? = null,
    var popularity: Int? = null,
    var playbackUrl: String? = null,
    var artworkUrl: String? = null,
    var musicSource: MusicSource = MusicSource.NOT_SPECIFIED,
    var timePlayed: Long = 0L
)