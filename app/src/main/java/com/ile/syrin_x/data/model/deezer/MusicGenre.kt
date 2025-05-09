package com.ile.syrin_x.data.model.deezer

data class MusicGenreResponse(
    val data: List<MusicGenre>
)

data class MusicGenre(
    val id: Int,
    val name: String,
    val picture: String?,
    val picture_small: String?,
    val picture_medium: String?,
    val picture_big: String?,
    val picture_xl: String?
)