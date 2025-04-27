package com.ile.syrin_x.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity
data class FavoriteArtistEntity(
    @PrimaryKey var id: String = "",
    var userId: String = "",
    var name: String = ""
)