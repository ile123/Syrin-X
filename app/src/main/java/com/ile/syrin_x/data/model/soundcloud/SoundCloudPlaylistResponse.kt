package com.ile.syrin_x.data.model.soundcloud

import androidx.compose.runtime.Immutable

@Immutable
data class SoundCloudPlaylistResponse(
    val collection: List<SoundCloudPlaylist>,
    val next_href: String?
)

@Immutable
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

@Immutable
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

@Immutable
data class SoundCloudPlaylistUser(
    val id: Long,
    val username: String,
    val permalink: String,
    val permalink_url: String,
    val avatar_url: String?
)

data class SoundCloudPlaylistById(
    val artworkUrl: String?,
    val createdAt: String,
    val description: String?,
    val downloadable: Boolean,
    val duration: Int,
    val ean: String?,
    val embeddableBy: String,
    val genre: String?,
    val id: Int,
    val kind: String,
    val label: String?,
    val labelId: String?,
    val labelName: String?,
    val lastModified: String,
    val license: String,
    val likesCount: Int,
    val permalink: String,
    val permalinkUrl: String,
    val playlistType: String?,
    val purchaseTitle: String?,
    val purchaseUrl: String?,
    val release: String?,
    val releaseDay: Int?,
    val releaseMonth: Int?,
    val releaseYear: Int?,
    val sharing: String,
    val streamable: Boolean,
    val tagList: String,
    val tags: String?,
    val title: String,
    val trackCount: Int,
    val tracks: List<SoundCloudPlaylistTrack>,
    val tracksUri: String,
    val type: String?,
    val uri: String,
    val user: SoundCloudPlaylistUser,
    val userId: Int
)