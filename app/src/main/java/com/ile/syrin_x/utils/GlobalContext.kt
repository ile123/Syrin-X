package com.ile.syrin_x.utils

object GlobalContext {
    object Tokens {
        var spotifyToken = ""
        var soundCloudToken = ""
    }
    val loggedInMusicSources: MutableList<String> = mutableListOf()
    var spotifyDeviceId = ""
}