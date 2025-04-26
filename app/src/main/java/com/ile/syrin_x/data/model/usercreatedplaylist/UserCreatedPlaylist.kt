package com.ile.syrin_x.data.model.usercreatedplaylist

data class UserCreatedPlaylist(
    val userCreatedPlaylistId: String,
    val userId: String,
    val name: String,
    val tracks: List<UserCreatedPlaylistTrack>
)

//Nekako tracks mora biti UserCreatedPlaylistTrack i UnitedTrack