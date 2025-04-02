package com.ile.syrin_x.data.model

import com.ile.syrin_x.data.model.spotify.SpotifyImage

data class UnifiedTrack(
    val id: String,
    val title: String?,
    val albumName: String?,
    val artists: List<String>?,
    val genre: String?,
    val durationMs: Int?,
    val explicit: Boolean?,
    val popularity: Int?,
    val playbackUrl: String?,
    val artworkUrl: String?
)

data class UnifiedPlaylist(
    val id: String,
    val name: String?,
    val description: String?,
    val images: List<SpotifyImage>?,
    val uri: String?,
    val ownerName: String?
)

data class UnifiedUser(
    val id: String,
    val name: String?,
    val avatarUrl: String?,
    val permalinkUrl: String?,
    val type: String?, // artist or normal user
)
