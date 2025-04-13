package com.ile.syrin_x.data.model.soundcloud

import androidx.compose.runtime.Immutable

@Immutable
data class SoundCloudTrackResponse(
    val collection: List<SoundCloudTrack>,
    val next_href: String?
)

@Immutable
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

@Immutable
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

@Immutable
data class SoundCloudSubscription(
    val product: SoundCloudProduct
)

@Immutable
data class SoundCloudProduct(
    val id: String,
    val name: String
)

@Immutable
data class SoundCloudBpm(
    val from: Long,
    val to: Long
)

@Immutable
data class SoundCloudDuration(
    val from: Long,
    val to: Long
)

@Immutable
data class SoundCloudCreatedAt(
    val from: String,
    val to: String
)

@Immutable
data class SoundCloudTrackById(
    val artwork_url: String?,
    val available_country_codes: List<String>?,
    val bpm: Double?,
    val comment_count: Int,
    val commentable: Boolean,
    val created_at: String,
    val description: String?,
    val download_count: Int,
    val download_url: String,
    val downloadable: Boolean,
    val duration: Int,
    val embeddable_by: String,
    val favoritings_count: Int,
    val genre: String,
    val id: Int,
    val isrc: String?,
    val key_signature: String?,
    val kind: String,
    val label_name: String,
    val license: String,
    val permalink_url: String,
    val playback_count: Int,
    val purchase_title: String?,
    val purchase_url: String?,
    val release: String?,
    val release_day: Int?,
    val release_month: Int?,
    val release_year: Int?,
    val reposts_count: Int,
    val secret_uri: String?,
    val sharing: String,
    val stream_url: String,
    val streamable: Boolean,
    val tag_list: String,
    val title: String,
    val uri: String,
    val user: SoundCloudTrackUser,
    val user_favorite: Boolean,
    val user_playback_count: Int,
    val waveform_url: String,
    val access: String
)

@Immutable
data class SoundCloudTrackStreamableUrls(
    val http_mp3_128_url: String,
    val hls_mp3_128_url: String,
    val hls_opus_64_url: String,
    val preview_mp3_128_url: String
)