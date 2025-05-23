package com.ile.syrin_x.data.model.soundcloud

import androidx.compose.runtime.Immutable

@Immutable
data class SoundCloudUserResponse(
    val collection: List<SoundCloudUser>,
    val next_href: String?
)

@Immutable
data class SoundCloudUser(
    val avatar_url: String?,
    val id: Long,
    val kind: String,
    val permalink_url: String,
    val uri: String,
    val username: String,
    val permalink: String,
    val created_at: String,
    val last_modified: String,
    val first_name: String?,
    val last_name: String?,
    val full_name: String?,
    val city: String?,
    val description: String?,
    val country: String?,
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
    val subscriptions: List<SoundCloudUserSubscription>?
)

@Immutable
data class SoundCloudUserSubscription(
    val product: SoundCloudUserProduct
)

@Immutable
data class SoundCloudUserProduct(
    val id: String,
    val name: String
)

@Immutable
data class SoundCloudUserTrack(
    val kind: String?,
    val id: Long?,
    val created_at: String?,
    val user_id: Long?,
    val duration: Long?,
    val comment_count: Int?,
    val commentable: Boolean?,
    val state: String?,
    val sharing: String?,
    val tag_list: String?,
    val permalink: String?,
    val streamable: Boolean?,
    val embeddable_by: String?,
    val downloadable: Boolean?,
    val likes_count: Int?,
    val playback_count: Int?,
    val download_count: Int?,
    val favoritings_count: Int?,
    val reposts_count: Int?,
    val policy: String?,
    val waveform_url: String?,
    val stream_url: String?,
    val download_url: String?,
    val title: String?,
    val artwork_url: String?,
    val user: SoundCloudUser?
)

@Immutable
data class SoundCloudUserTracksResponse(
    val collection: List<SoundCloudUserTrack>?,
    val next_href: String?
)