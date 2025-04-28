package com.ile.syrin_x.data.model.usercreatedplaylist

import com.ile.syrin_x.data.model.UnifiedTrack

data class UserCreatedPlaylist(
    val userCreatedPlaylistId: String,
    val userId: String,
    val name: String,
    val tracks: List<UserCreatedPlaylistTrack>
)