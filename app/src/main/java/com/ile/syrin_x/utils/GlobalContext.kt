package com.ile.syrin_x.utils

import com.ile.syrin_x.data.enums.MusicSource

object GlobalContext {
    object Tokens {
        var spotifyToken = ""
        var soundCloudToken = ""
    }
    val loggedInMusicSources: MutableList<String> = mutableListOf()
    var musicSource = MusicSource.NONE
}