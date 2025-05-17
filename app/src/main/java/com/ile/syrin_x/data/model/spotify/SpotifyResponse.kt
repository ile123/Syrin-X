package com.ile.syrin_x.data.model.spotify

import androidx.compose.runtime.Immutable

@Immutable
data class SpotifyResponse(
    val tracks: SpotifyTracksResponse?,
    val artists: SpotifyArtistsResponse?,
    val albums: SpotifyAlbumsResponse?,
    val playlists: SpotifyPlaylistsResponse?
)

@Immutable
data class SpotifyTracksResponse(
    val href: String?,
    val limit: Int?,
    val next: String?,
    val offset: Int?,
    val previous: String?,
    val total: Int?,
    val items: List<SpotifyTrackItem>?
)

@Immutable
data class SpotifyTrackItem(
    val album: SpotifyAlbum?,
    val artists: List<SpotifyArtist>?,
    val available_markets: List<String>?,
    val disc_number: Int?,
    val duration_ms: Int?,
    val explicit: Boolean?,
    val external_ids: SpotifyExternalIds?,
    val external_urls: SpotifyExternalUrls?,
    val href: String?,
    val id: String?,
    val is_playable: Boolean?,
    val name: String?,
    val popularity: Int?,
    val preview_url: String?,
    val track_number: Int?,
    val type: String?,
    val uri: String?,
    val is_local: Boolean?
)

@Immutable
data class SpotifyExternalIds(
    val isrc: String?
)

data class SpotifyExternalUrls(
    val spotify: String?
)

@Immutable
data class SpotifyAlbum(
    val album_type: String?,
    val total_tracks: Int?,
    val available_markets: List<String>?,
    val external_urls: SpotifyExternalUrls?,
    val href: String?,
    val id: String?,
    val images: List<SpotifyImage>?,
    val name: String?,
    val release_date: String?,
    val release_date_precision: String?,
    val type: String?,
    val uri: String?,
    val artists: List<SpotifyArtist>?,
    val is_playable: Boolean?
)

@Immutable
data class SpotifyImage(
    val url: String?,
    val height: Int?,
    val width: Int?
)

@Immutable
data class SpotifyArtist(
    val external_urls: SpotifyExternalUrls?,
    val href: String?,
    val id: String?,
    val name: String?,
    val type: String?,
    val uri: String?
)

@Immutable
data class SpotifyArtistsResponse(
    val href: String?,
    val limit: Int?,
    val next: String?,
    val offset: Int?,
    val previous: String?,
    val total: Int?,
    val items: List<SpotifyArtist>?
)

@Immutable
data class SpotifyAlbumsResponse(
    val href: String?,
    val limit: Int?,
    val next: String?,
    val offset: Int?,
    val previous: String?,
    val total: Int?,
    val items: List<SpotifyAlbum>?
)

@Immutable
data class SpotifyPlaylistsResponse(
    val href: String?,
    val limit: Int?,
    val next: String?,
    val offset: Int?,
    val previous: String?,
    val total: Int?,
    val items: List<SpotifyPlaylist>?
)

@Immutable
data class SpotifyPlaylist(
    val collaborative: Boolean?,
    val description: String?,
    val external_urls: SpotifyExternalUrls?,
    val href: String?,
    val id: String?,
    val images: List<SpotifyImage>?,
    val name: String?,
    val owner: SpotifyPlaylistOwner?,
    val public: Boolean?,
    val snapshot_id: String?,
    val tracks: SpotifyPlaylistTracks?,
    val type: String?,
    val uri: String?,
    val primary_color: String?
)

@Immutable
data class SpotifyPlaylistOwner(
    val external_urls: SpotifyExternalUrls?,
    val href: String?,
    val id: String?,
    val type: String?,
    val uri: String?,
    val display_name: String?
)

@Immutable
data class SpotifyPlaylistTracks(
    val href: String?,
    val total: Int?
)

@Immutable
data class SpotifyTrackDetails(
    val album: SpotifyAlbum?,
    val artists: List<SpotifyArtist>?,
    val available_markets: List<String>?,
    val disc_number: Int?,
    val duration_ms: Int?,
    val explicit: Boolean?,
    val external_ids: SpotifyExternalIds?,
    val external_urls: SpotifyExternalUrls?,
    val href: String?,
    val id: String?,
    val is_playable: Boolean?,
    val name: String?,
    val popularity: Int?,
    val preview_url: String?,
    val track_number: Int?,
    val type: String?,
    val uri: String?,
    val is_local: Boolean?,
)

@Immutable
data class SpotifyPlaylistById(
    val collaborative: Boolean?,
    val description: String?,
    val external_urls: SpotifyExternalUrls?,
    val href: String?,
    val id: String?,
    val images: List<SpotifyImage>?,
    val name: String?,
    val owner: SpotifyPlaylistOwner?,
    val public: Boolean?,
    val snapshot_id: String?,
    val tracks: SpotifyPlaylistByIdTracksResponse?,
    val type: String?,
    val uri: String?
)

@Immutable
data class SpotifyPlaylistByIdTracksResponse(
    val href: String?,
    val limit: Int?,
    val next: String?,
    val offset: Int?,
    val previous: String?,
    val total: Int?,
    val items: List<SpotifyPlaylistByIdTrackItem>?
)

@Immutable
data class SpotifyPlaylistByIdTrackItem(
    val added_at: String?,
    val is_local: Boolean?,
    val track: SpotifyPlaylistByIdTrackDetails?
)

@Immutable
data class SpotifyPlaylistByIdTrackDetails(
    val album: SpotifyAlbum?,
    val artists: List<SpotifyArtist>?,
    val available_markets: List<String>?,
    val disc_number: Int?,
    val duration_ms: Int?,
    val explicit: Boolean?,
    val external_ids: SpotifyExternalIds?,
    val external_urls: SpotifyExternalUrls?,
    val href: String?,
    val id: String?,
    val is_playable: Boolean?,
    val name: String?,
    val popularity: Int?,
    val preview_url: String?,
    val track_number: Int?,
    val type: String?,
    val uri: String?,
    val is_local: Boolean?
)

@Immutable
data class SpotifyAlbumByIdResponse(
    val album_type: String?,
    val total_tracks: Int?,
    val available_markets: List<String>?,
    val external_urls: SpotifyExternalUrls?,
    val href: String?,
    val id: String?,
    val images: List<SpotifyImage>?,
    val name: String?,
    val release_date: String?,
    val release_date_precision: String?,
    val type: String?,
    val uri: String?,
    val artists: List<SpotifyArtist>?,
    val tracks: SpotifyTracksResponse?
)

data class SpotifyArtistSongsResponse(
    val tracks: List<SpotifyArtistSongsTrack>
)

data class SpotifyArtistSongsTrack(
    val album: SpotifyArtistSongsAlbum,
    val artists: List<SpotifyArtistSongsArtist>,
    val available_markets: List<String>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val href: String,
    val id: String,
    val is_playable: Boolean,
    val name: String,
    val popularity: Int,
    val preview_url: String?,
    val track_number: Int,
    val type: String,
    val uri: String,
    val is_local: Boolean
)

data class SpotifyArtistSongsAlbum(
    val album_type: String,
    val total_tracks: Int,
    val available_markets: List<String>,
    val href: String,
    val id: String,
    val images: List<SpotifyArtistSongsImage>,
    val name: String,
    val release_date: String,
    val release_date_precision: String,
    val type: String,
    val uri: String,
    val artists: List<SpotifyArtistSongsArtist>
)

data class SpotifyArtistSongsImage(
    val url: String,
    val height: Int,
    val width: Int
)

data class SpotifyArtistSongsArtist(
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)