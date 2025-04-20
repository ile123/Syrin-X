package com.ile.syrin_x.data.model.spotify

data class SpotifyPlaybackState(
    val is_playing: Boolean,

    val progress_ms: Long,

    val item: SpotifyPlaybackTrack?,

    val repeat_state: String,

    val device: SpotifyPlaybackDevice
)

data class SpotifyPlaybackTrack(
    val name: String,
    val artists: List<SpotifyPlaybackArtist>,
    val duration_ms: Long,
    val uri: String,
    val album: SpotifyPlaybackAlbum
)

data class SpotifyPlaybackArtist(
    val name: String
)

data class SpotifyPlaybackAlbum(
    val name: String,
    val images: List<SpotifyPlaybackImage>
)

data class SpotifyPlaybackImage(
    val url: String,
    val height: Int?,
    val width: Int?
)

data class SpotifyPlaybackDevice(
    val id: String,
    val name: String,
    val type: String,
    val is_active: Boolean
)

data class PlaybackRequestBody(
    val uris: List<String?> = mutableListOf(),

    val context_uri: String? = null,

    val offset: PlaybackRequestOffset? = null,

    val position_ms: Long? = null
)

data class PlaybackRequestOffset(
    val position: Int? = null,
    val uri: String? = null
)

data class SpotifyDevicesResponse(
    val devices: List<SpotifyDevice>
)

data class SpotifyDevice(
    val id: String,
    val name: String,
    val type: String,
    val is_active: Boolean,
    val is_restricted: Boolean,
    val volume_percent: Int
)

data class SpotifyRestrictions(
    val reason: String
)

data class SpotifyPlaybackContext(
    val type: String,
    val href: String,
    val external_urls: SpotifyExternalUrls,
    val uri: String
)

data class SpotifyPlaybackActions(
    val interrupting_playback: Boolean,
    val pausing: Boolean,
    val resuming: Boolean,
    val seeking: Boolean,
    val skipping_next: Boolean,
    val skipping_prev: Boolean,
    val toggling_repeat_context: Boolean,
    val toggling_shuffle: Boolean,
    val toggling_repeat_track: Boolean,
    val transferring_playback: Boolean
)

data class SpotifyLinkedFrom(
    val external_urls: SpotifyExternalUrls?,
    val href: String?,
    val id: String?,
    val uri: String?
)

data class SpotifyPlaybackArtistFull(
    val external_urls: SpotifyExternalUrls,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)

data class SpotifyPlaybackAlbumFull(
    val album_type: String,
    val total_tracks: Int,
    val available_markets: List<String>,
    val external_urls: SpotifyExternalUrls,
    val href: String,
    val id: String,
    val images: List<SpotifyPlaybackImage>,
    val name: String,
    val release_date: String,
    val release_date_precision: String,
    val restrictions: SpotifyRestrictions?,
    val type: String,
    val uri: String,
    val artists: List<SpotifyPlaybackArtistFull>
)

data class SpotifyPlaybackTrackFull(
    val album: SpotifyPlaybackAlbumFull,
    val artists: List<SpotifyPlaybackArtistFull>,
    val available_markets: List<String>,
    val disc_number: Int,
    val duration_ms: Long,
    val explicit: Boolean,
    val external_ids: SpotifyExternalIds,
    val external_urls: SpotifyExternalUrls,
    val href: String,
    val id: String,
    val is_playable: Boolean,
    val linked_from: SpotifyLinkedFrom?,
    val restrictions: SpotifyRestrictions?,
    val name: String,
    val popularity: Int,
    val preview_url: String?,
    val track_number: Int,
    val type: String,
    val uri: String,
    val is_local: Boolean
)

data class SpotifyPlaybackStateResponse(
    val device: SpotifyPlaybackDevice,
    val repeat_state: String,
    val shuffle_state: Boolean,
    val context: SpotifyPlaybackContext?,
    val timestamp: Long,
    val progress_ms: Long,
    val is_playing: Boolean,
    val item: SpotifyPlaybackTrackFull?,
    val currently_playing_type: String,
    val actions: SpotifyPlaybackActions
)
