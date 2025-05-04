package com.ile.syrin_x.data.model.deezer

data class TrackByGenreResponse(
    val data: List<TrackByGenre>
)

data class TrackByGenre(
    val id: Long,
    val title: String,
    val duration: Int,
    val rank: Int,
    val preview: String,
    val artist: TrackByGenreArtist,
    val album: TrackByGenreAlbum
)

data class TrackByGenreArtist(
    val id: Int,
    val name: String,
    val picture: String?,
    val picture_small: String?,
    val picture_medium: String?,
    val picture_big: String?,
    val picture_xl: String?
)

data class TrackByGenreAlbum(
    val id: Int,
    val title: String,
    val cover: String?,
    val cover_small: String?,
    val cover_medium: String?,
    val cover_big: String?,
    val cover_xl: String?
)