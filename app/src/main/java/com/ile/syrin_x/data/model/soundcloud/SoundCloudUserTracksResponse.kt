package com.ile.syrin_x.data.model.soundcloud

data class SoundCloudTracksByUserResponse(
    val collection: List<SoundCloudUserTracksTrack>,
    val next_href: String?
)

data class SoundCloudUserTracksTrack(
    val kind: String,
    val id: Long,
    val urn: String,
    val created_at: String,
    val duration: Long,
    val commentable: Boolean,
    val comment_count: Int,
    val sharing: String,
    val tag_list: String,
    val streamable: Boolean,
    val embeddable_by: String,
    val purchase_url: String?,
    val purchase_title: String?,
    val genre: String?,
    val title: String,
    val description: String?,
    val label_name: String?,
    val release: String?,
    val key_signature: String?,
    val isrc: String?,
    val bpm: Float?,
    val release_year: Int?,
    val release_month: Int?,
    val release_day: Int?,
    val license: String,
    val uri: String,
    val user: SoundCloudTracksUser,
    val permalink_url: String,
    val artwork_url: String?,
    val stream_url: String,
    val download_url: String?,
    val waveform_url: String,
    val available_country_codes: List<String>?,
    val secret_uri: String?,
    val user_favorite: Boolean?,
    val user_playback_count: Int?,
    val playback_count: Int,
    val download_count: Int,
    val favoritings_count: Int,
    val reposts_count: Int,
    val downloadable: Boolean,
    val access: String,
    val policy: String?,
    val monetization_model: String?,
    val metadata_artist: String
)

data class SoundCloudTracksUser(
    val avatar_url: String,
    val id: Long,
    val urn: String,
    val kind: String,
    val permalink_url: String,
    val uri: String,
    val username: String,
    val permalink: String,
    val created_at: String,
    val last_modified: String,
    val first_name: String,
    val last_name: String,
    val full_name: String,
    val city: String?,
    val description: String,
    val country: String,
    val track_count: Int,
    val public_favorites_count: Int,
    val reposts_count: Int,
    val followers_count: Int,
    val followings_count: Int,
    val plan: String,
    val myspace_name: String?,
    val discogs_name: String?,
    val website_title: String?,
    val website: String?,
    val comments_count: Int,
    val online: Boolean,
    val likes_count: Int,
    val playlist_count: Int,
    val subscriptions: List<SoundCloudTracksSubscription>
)

data class SoundCloudTracksSubscription(
    val product: SoundCloudTracksProduct
)

data class SoundCloudTracksProduct(
    val id: String,
    val name: String
)