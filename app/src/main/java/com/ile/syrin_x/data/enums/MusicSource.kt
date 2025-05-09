package com.ile.syrin_x.data.enums

enum class MusicSource {
    SPOTIFY, SOUNDCLOUD, NOT_SPECIFIED
}

enum class MusicCategory(val displayName: String) {
    TRACKS("Tracks"),
    PLAYLISTS("Playlists"),
    ALBUMS("Albums"),
    USERS("Users")
}