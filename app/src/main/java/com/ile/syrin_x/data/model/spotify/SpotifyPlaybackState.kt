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