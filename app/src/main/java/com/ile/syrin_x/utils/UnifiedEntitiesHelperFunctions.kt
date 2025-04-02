package com.ile.syrin_x.utils

import com.ile.syrin_x.data.model.UnifiedPlaylist
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.UnifiedUser
import com.ile.syrin_x.data.model.soundcloud.SoundCloudPlaylist
import com.ile.syrin_x.data.model.soundcloud.SoundCloudTrack
import com.ile.syrin_x.data.model.soundcloud.SoundCloudUser
import com.ile.syrin_x.data.model.spotify.SpotifyArtist
import com.ile.syrin_x.data.model.spotify.SpotifyPlaylist
import com.ile.syrin_x.data.model.spotify.SpotifyTrackItem

fun List<SoundCloudTrack>.toSoundCloudUnifiedTracks(): List<UnifiedTrack> = this
    .map { track ->
    UnifiedTrack(
        id = track.id.toString(),
        title = track.title,
        albumName = null,
        artists = listOf(track.user.username),
        genre = track.genre,
        durationMs = track.duration,
        explicit = null,
        popularity = track.playback_count,
        playbackUrl = track.stream_url,
        artworkUrl = track.artwork_url
    )
}

fun List<SpotifyTrackItem>.toSpotifyUnifiedTracks(): List<UnifiedTrack> = this
    .filter { track -> track?.id != null }
    .map { track ->
    UnifiedTrack(
        id = track.id ?: "",
        title = track.name,
        albumName = track.album?.name,
        artists = track.artists?.map { it.name.orEmpty() },
        genre = track.album?.album_type,
        durationMs = track.duration_ms,
        explicit = track.explicit,
        popularity = track.popularity,
        playbackUrl = track.preview_url,
        artworkUrl = track.album?.images?.firstOrNull()?.url
    )
}

fun List<SoundCloudPlaylist>.toSoundCloudUnifiedPlaylists(): List<UnifiedPlaylist> = this
    .map { playlist ->
    UnifiedPlaylist(
        id = playlist.id.toString(),
        name = playlist.title,
        description = playlist.description,
        images = null,
        uri = playlist.permalink_url,
        ownerName = playlist.user.username
    )
}

fun List<SpotifyPlaylist>.toSpotifyUnifiedPlaylists(): List<UnifiedPlaylist> = this
    .filter { playlist -> playlist?.id != null }
    .map { playlist ->
    UnifiedPlaylist(
        id = playlist.id ?: "",
        name = playlist.name,
        description = playlist.description,
        images = playlist.images,
        uri = playlist.uri,
        ownerName = playlist.owner?.display_name
    )
}

fun List<SoundCloudUser>.toSoundCloudUnifiedUsers(): List<UnifiedUser> = this
    .map { user ->
    UnifiedUser(
        id = user.id.toString(),
        name = user.username,
        avatarUrl = user.avatar_url,
        permalinkUrl = user.permalink_url,
        type = if (user.kind == "artist") "artist" else "user"
    )
}

fun List<SpotifyArtist>.toSpotifyUnifiedUsers(): List<UnifiedUser> = this
    .filter { artist -> artist?.id != null }
    .map { artist ->
    UnifiedUser(
        id = artist.id ?: "",
        name = artist.name,
        avatarUrl = null,
        permalinkUrl = artist.external_urls?.spotify,
        type = "artist"
    )
}
