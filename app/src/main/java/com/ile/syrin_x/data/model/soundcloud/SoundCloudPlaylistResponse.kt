package com.ile.syrin_x.data.model.soundcloud

data class SoundCloudPlaylistResponse(
    val collection: List<SoundCloudPlaylist>,
    val next_href: String?
)

data class SoundCloudPlaylist(
    val id: Long,
    val title: String,
    val description: String?,
    val genre: String?,
    val duration: Long,
    val permalink: String,
    val permalink_url: String,
    val sharing: String,
    val streamable: Boolean,
    val tracks: List<SoundCloudPlaylistTrack>,
    val user: SoundCloudPlaylistUser
)

data class SoundCloudPlaylistTrack(
    val id: Long,
    val title: String,
    val duration: Long,
    val genre: String?,
    val sharing: String,
    val streamable: Boolean,
    val playback_count: Long,
    val permalink_url: String,
    val artwork_url: String?,
    val user: SoundCloudUser
)

data class SoundCloudPlaylistUser(
    val id: Long,
    val username: String,
    val permalink: String,
    val permalink_url: String,
    val avatar_url: String?
)
