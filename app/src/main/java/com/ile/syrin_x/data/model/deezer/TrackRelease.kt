package com.ile.syrin_x.data.model.deezer

data class TrackReleaseAlbumResponse(
    val data: List<TrackReleaseAlbum>
)

data class TrackReleaseAlbum(
    val id: Long,
    val title: String,
    val release_date: String
)

data class TrackReleaseTrackListResponse(
    val data: List<TrackReleaseTrack>
)

data class TrackReleaseTrack(
    val id: Long,
    val title: String,
    val artist: TrackReleaseArtistSummary,
    val release_date: String
)

data class TrackReleaseArtistSummary(
    val id: Long,
    val name: String
)

data class TrackReleaseArtistSearchResponse(
    val data: List<TrackReleaseArtist>
)

data class TrackReleaseArtist(
    val id: Long,
    val name: String,
    val picture: String?,
    val tracklist: String?
)