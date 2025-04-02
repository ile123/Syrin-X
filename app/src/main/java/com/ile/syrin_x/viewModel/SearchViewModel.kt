package com.ile.syrin_x.viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedPlaylist
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.UnifiedUser
import com.ile.syrin_x.data.model.soundcloud.SoundCloudPlaylist
import com.ile.syrin_x.data.model.soundcloud.SoundCloudPlaylistResponse
import com.ile.syrin_x.data.model.soundcloud.SoundCloudPlaylistTrack
import com.ile.syrin_x.data.model.soundcloud.SoundCloudTrack
import com.ile.syrin_x.data.model.soundcloud.SoundCloudTrackResponse
import com.ile.syrin_x.data.model.soundcloud.SoundCloudUser
import com.ile.syrin_x.data.model.soundcloud.SoundCloudUserResponse
import com.ile.syrin_x.data.model.spotify.SpotifyAlbum
import com.ile.syrin_x.data.model.spotify.SpotifyResponse
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.SearchPlaylistsSoundCloudUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.SearchTracksSoundCloudUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.SearchUsersSoundCloudUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.SearchAllSpotifyUseCase
import com.ile.syrin_x.utils.GlobalContext
import com.ile.syrin_x.utils.toSoundCloudUnifiedPlaylists
import com.ile.syrin_x.utils.toSoundCloudUnifiedTracks
import com.ile.syrin_x.utils.toSoundCloudUnifiedUsers
import com.ile.syrin_x.utils.toSpotifyUnifiedPlaylists
import com.ile.syrin_x.utils.toSpotifyUnifiedTracks
import com.ile.syrin_x.utils.toSpotifyUnifiedUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchAllSpotifyUseCase: SearchAllSpotifyUseCase,
    private val searchTracksSoundCloudUseCase: SearchTracksSoundCloudUseCase,
    private val searchPlaylistsSoundCloudUseCase: SearchPlaylistsSoundCloudUseCase,
    private val searchUsersSoundCloudUseCase: SearchUsersSoundCloudUseCase
): ViewModel() {

    private val _searchFlow = MutableSharedFlow<Response<Any>>()
    val searchFlow = _searchFlow

    val offsets = mutableMapOf(
        "tracks" to 0,
        "albums" to 0,
        "playlists" to 0,
        "users" to 0
    )

    val searchedTracks: MutableList<UnifiedTrack> = mutableListOf()
    val searchedPlaylists: MutableList<UnifiedPlaylist> = mutableListOf()
    val searchedAlbums: MutableList<SpotifyAlbum> = mutableListOf()
    val searchedUsers: MutableList<UnifiedUser> = mutableListOf()

    fun clearPreviouslyFetchedContent() {
        searchedTracks.clear()
        searchedPlaylists.clear()
        searchedAlbums.clear()
        searchedUsers.clear()
    }

    fun searchAll(keyword: String, musicSource: String) = viewModelScope.launch {
        clearPreviouslyFetchedContent()
        when (musicSource) {
            "Spotify" -> {
                GlobalContext.musicSource = MusicSource.SPOTIFY
                searchAllSpotifyUseCase(
                    keyword,
                    GlobalContext.Tokens.spotifyToken
                ).collect { response ->
                    when (response) {
                        is Response.Loading -> {
                        }
                        is Response.Success<*> -> {
                            if (response.data is SpotifyResponse) {
                                val spotifyResponse = response.data
                                searchedTracks.addAll(spotifyResponse.tracks?.items?.toSpotifyUnifiedTracks() ?: emptyList())
                                searchedPlaylists.addAll(spotifyResponse.playlists?.items?.toSpotifyUnifiedPlaylists() ?: emptyList())
                                searchedUsers.addAll(spotifyResponse.artists?.items?.toSpotifyUnifiedUsers() ?: emptyList())
                                spotifyResponse.albums?.items?.let { searchedAlbums.addAll(it) }

                                _searchFlow.emit(Response.Success(true))
                            }
                        }
                        is Response.Error -> {
                            Log.d("SearchViewModel", response.message)
                        }
                    }
                    _searchFlow.emit(response)
                }
            }
            "SoundCloud" -> {
                val accessToken = GlobalContext.Tokens.soundCloudToken
                GlobalContext.musicSource = MusicSource.SOUNDCLOUD
                combine(
                    searchTracksSoundCloudUseCase(keyword, 10, 0, accessToken),
                    searchUsersSoundCloudUseCase(keyword, 10, 0, accessToken),
                    searchPlaylistsSoundCloudUseCase(keyword, 10, 0, accessToken)
                ) { tracks, users, playlists ->
                    when {
                        tracks is Response.Success<*> &&
                                users is Response.Success<*> &&
                                playlists is Response.Success<*> -> {
                            val trackResponse = tracks.data as? SoundCloudTrackResponse
                            searchedTracks.addAll(trackResponse?.collection?.toSoundCloudUnifiedTracks() ?: emptyList())

                            val userResponse = users.data as? SoundCloudUserResponse
                            searchedUsers.addAll(userResponse?.collection?.toSoundCloudUnifiedUsers() ?: emptyList())

                            val playlistResponse = playlists.data as? SoundCloudPlaylistResponse
                            searchedPlaylists.addAll(playlistResponse?.collection?.toSoundCloudUnifiedPlaylists() ?: emptyList())

                            Response.Success(true)
                        }
                        tracks is Response.Error -> tracks
                        users is Response.Error -> users
                        playlists is Response.Error -> playlists
                        else -> Response.Loading
                    }

                }.collect { response ->
                    _searchFlow.emit(response)
                }
            }
        }
    }

    fun searchTracks(keyword: String, limit: Long, offset: Long, musicSource: String) {

    }

    fun searchPlaylists(keyword: String, limit: Long, offset: Long, musicSource: String) {

    }

    fun searchAlbums(keyword: String, limit: Long, offset: Long, musicSource: String) {

    }

    fun searchUsers(keyword: String, limit: Long, offset: Long, musicSource: String) {

    }
}