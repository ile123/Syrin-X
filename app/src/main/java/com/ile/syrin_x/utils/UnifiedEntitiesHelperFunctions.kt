package com.ile.syrin_x.utils

import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedPlaylist
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.UnifiedUser
import com.ile.syrin_x.data.model.soundcloud.SoundCloudPlaylist
import com.ile.syrin_x.data.model.soundcloud.SoundCloudPlaylistById
import com.ile.syrin_x.data.model.soundcloud.SoundCloudPlaylistTrack
import com.ile.syrin_x.data.model.soundcloud.SoundCloudTrack
import com.ile.syrin_x.data.model.soundcloud.SoundCloudTrackById
import com.ile.syrin_x.data.model.soundcloud.SoundCloudUser
import com.ile.syrin_x.data.model.spotify.SpotifyAlbum
import com.ile.syrin_x.data.model.spotify.SpotifyAlbumByIdResponse
import com.ile.syrin_x.data.model.spotify.SpotifyArtist
import com.ile.syrin_x.data.model.spotify.SpotifyImage
import com.ile.syrin_x.data.model.spotify.SpotifyPlaylist
import com.ile.syrin_x.data.model.spotify.SpotifyPlaylistById
import com.ile.syrin_x.data.model.spotify.SpotifyPlaylistByIdTrackItem
import com.ile.syrin_x.data.model.spotify.SpotifyTrackDetails
import com.ile.syrin_x.data.model.spotify.SpotifyTrackItem

val fallbackImageUrl = "https://static.vecteezy.com/system/resources/previews/023/181/823/original/no-result-document-file-data-not-found-concept-illustration-line-icon-design-editable-eps10-vector.jpg"

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
        artworkUrl = track.artwork_url ?: fallbackImageUrl,
        musicSource = MusicSource.SOUNDCLOUD
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
        artworkUrl = track.album?.images?.firstOrNull()?.url ?: fallbackImageUrl,
        musicSource = MusicSource.SPOTIFY
    )
}

fun List<SoundCloudPlaylistTrack>.toSoundCloudPlaylistItemUnifiedTracks(): List<UnifiedTrack> = this
    .map { track ->
        UnifiedTrack(
            id = track.id.toString(),
            title = track.title,
            albumName = null,
            artists = listOf(track.user.username),
            genre = track.genre,
            durationMs = track.duration.toInt(),
            explicit = null,
            popularity = track.playback_count.toInt(),
            playbackUrl = track.permalink_url,
            artworkUrl = track.artwork_url ?: fallbackImageUrl,
            musicSource = MusicSource.SOUNDCLOUD
        )
    }

fun List<SoundCloudPlaylist>.toSoundCloudUnifiedPlaylists(): List<UnifiedPlaylist> = this
    .map { playlist ->
    UnifiedPlaylist(
        id = playlist.id.toString(),
        name = playlist.title,
        description = playlist.description,
        images = listOf(SpotifyImage(fallbackImageUrl, 0, 0)),
        uri = playlist.permalink_url,
        ownerName = playlist.user.username,
        musicSource = MusicSource.SOUNDCLOUD
    )
}

fun List<SpotifyPlaylist>.toSpotifyUnifiedPlaylists(): List<UnifiedPlaylist> = this
    .filter { playlist -> playlist?.id != null }
    .map { playlist ->
    UnifiedPlaylist(
        id = playlist.id ?: fallbackImageUrl,
        name = playlist.name,
        description = playlist.description,
        images = playlist.images,
        uri = playlist.uri,
        ownerName = playlist.owner?.display_name,
        musicSource = MusicSource.SPOTIFY
    )
}

fun List<SoundCloudUser>.toSoundCloudUnifiedUsers(): List<UnifiedUser> = this
    .map { user ->
    UnifiedUser(
        id = user.id.toString(),
        name = user.username,
        avatarUrl = user.avatar_url ?: fallbackImageUrl,
        permalinkUrl = user.permalink_url,
        type = if (user.kind == "artist") "artist" else "user",
        musicSource = MusicSource.SOUNDCLOUD
    )
}

fun List<SpotifyArtist>.toSpotifyUnifiedUsers(): List<UnifiedUser> = this
    .filter { artist -> artist?.id != null }
    .map { artist ->
    UnifiedUser(
        id = artist.id ?: "",
        name = artist.name,
        avatarUrl = fallbackImageUrl,
        permalinkUrl = artist.external_urls?.spotify,
        type = "artist",
        musicSource = MusicSource.SPOTIFY
    )
}

fun List<SpotifyPlaylistByIdTrackItem>.toSpotifyPlaylistByIdTrackItemToUnifiedTracks(): List<UnifiedTrack> = this
    .filter { x -> x.track?.id != null }
    .map { x ->
        UnifiedTrack(
            id = x.track?.id ?: "",
            title = x.track?.name,
            albumName = x.track?.album?.name,
            artists = x.track?.artists?.map { it.name.orEmpty() },
            genre = x.track?.album?.album_type,
            durationMs = x.track?.duration_ms,
            explicit = x.track?.explicit,
            popularity = x.track?.popularity,
            playbackUrl = x.track?.preview_url,
            artworkUrl = x.track?.album?.images?.firstOrNull()?.url ?: fallbackImageUrl,
            musicSource = MusicSource.SPOTIFY
        )
    }

fun fromSpotifyTrackToUnifiedTrack(spotifyTrackItem: SpotifyTrackDetails?): UnifiedTrack{
    val id = spotifyTrackItem?.id ?: ""
    var artists = emptyList<String>()
    if(spotifyTrackItem?.artists != null) {
        artists = listOf(spotifyTrackItem.artists.map { item -> item.name }.toString())
    }

    return UnifiedTrack(
        id = id,
        title = spotifyTrackItem?.name ?: "",
        albumName = spotifyTrackItem?.album?.name,
        artists = artists,
        genre = "",
        durationMs = spotifyTrackItem?.duration_ms,
        explicit = spotifyTrackItem?.explicit,
        popularity = spotifyTrackItem?.popularity,
        playbackUrl = "spotify:track:${id}",
        artworkUrl = spotifyTrackItem?.album?.images?.get(0)?.url ?: fallbackImageUrl,
        musicSource = MusicSource.SPOTIFY
    )
}

fun fromSoundCloudTrackToUnifiedTrack(soundCloudTrackById: SoundCloudTrackById?): UnifiedTrack {
    val id = soundCloudTrackById?.id.toString()
    var artists = emptyList<String>()
    if(soundCloudTrackById?.user != null) {
        artists = listOf(soundCloudTrackById.user.username)
    }

    return UnifiedTrack(
        id = id,
        title = soundCloudTrackById?.title,
        albumName = "",
        artists = artists,
        genre = soundCloudTrackById?.genre,
        durationMs = soundCloudTrackById?.duration,
        explicit = false,
        popularity = soundCloudTrackById?.playback_count,
        playbackUrl = soundCloudTrackById?.stream_url,
        artworkUrl = soundCloudTrackById?.artwork_url ?: fallbackImageUrl,
        musicSource = MusicSource.SOUNDCLOUD
    )
}

fun fromSpotifyPlaylistToUnifiedPlaylist(spotifyPlaylistById: SpotifyPlaylistById): UnifiedPlaylist {
    val id = spotifyPlaylistById.id.toString()
    var images = mutableListOf(SpotifyImage(fallbackImageUrl, 0, 0))
    if(spotifyPlaylistById.images != null) {
        images.clear()
        images.addAll(spotifyPlaylistById.images)
    }
    return UnifiedPlaylist(
        id = id,
        name = spotifyPlaylistById.name,
        description = spotifyPlaylistById.description,
        images = images,
        uri = spotifyPlaylistById.uri,
        ownerName = spotifyPlaylistById.owner?.display_name,
        musicSource = MusicSource.SPOTIFY,
    )
}

fun fromSoundCloudPlaylistToUnifiedPlaylist(soundCloudPlaylistById: SoundCloudPlaylistById): UnifiedPlaylist {
    val id = soundCloudPlaylistById.id.toString()
    val images = mutableListOf(SpotifyImage(fallbackImageUrl, 0, 0))
    if(soundCloudPlaylistById.artworkUrl != null) {
        images.clear()
        images.add(SpotifyImage(soundCloudPlaylistById.artworkUrl, 0, 0))
    }
    return UnifiedPlaylist(
        id = id,
        name = soundCloudPlaylistById.title,
        description = soundCloudPlaylistById.description,
        images = images,
        uri = soundCloudPlaylistById.uri,
        ownerName = soundCloudPlaylistById.user.username,
        musicSource = MusicSource.SOUNDCLOUD
    )
}