package com.ile.syrin_x.data.model.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PlaylistWithTracks(
    @Embedded val playlist: UserCreatedPlaylistEntity,

    @Relation(
        parentColumn = "userCreatedPlaylistId",
        entityColumn = "playlistId"
    )
    val tracks: List<UserCreatedPlaylistTrackEntity>
)