package com.ile.syrin_x.data.model.soundcloud

data class SoundCloudTrackResponse(
    val collection: List<SoundCloudTrack>,
    val next_href: String?
)

data class SoundCloudTrack(
    val kind: String,
    val id: Long,
    val created_at: String,
    val duration: Int,
    val genre: String?,
    val title: String,
    val description: String?,
    val user: SoundCloudTrackUser,
    val permalink_url: String,
    val artwork_url: String?,
    val stream_url: String?,
    val playback_count: Int,
    val favoritings_count: Int,
    val reposts_count: Int
)

data class SoundCloudTrackUser(
    val id: Long,
    val username: String,
    val permalink_url: String,
    val avatar_url: String?,
    val city: String?,
    val description: String?,
    val track_count: Int,
    val followers_count: Int,
    val playlist_count: Int,
    val subscriptions: List<SoundCloudSubscription>?
)

data class SoundCloudSubscription(
    val product: SoundCloudProduct
)

data class SoundCloudProduct(
    val id: String,
    val name: String
)

data class SoundCloudBpm(
    val from: Long,
    val to: Long
)

data class SoundCloudDuration(
    val from: Long,
    val to: Long
)

data class SoundCloudCreatedAt(
    val from: String,
    val to: String
)