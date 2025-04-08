package com.ile.syrin_x.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedPlaylist
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.soundcloud.SoundCloudPlaylistById
import com.ile.syrin_x.data.model.spotify.SpotifyPlaylistById
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.GetPlaylistByIdSoundCloudUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.GetPlaylistByIdSpotifyUseCase
import com.ile.syrin_x.utils.GlobalContext
import com.ile.syrin_x.utils.fromSoundCloudPlaylistToUnifiedPlaylist
import com.ile.syrin_x.utils.fromSpotifyPlaylistToUnifiedPlaylist
import com.ile.syrin_x.utils.toSoundCloudPlaylistItemUnifiedTracks
import com.ile.syrin_x.utils.toSpotifyPlaylistByIdTrackItemToUnifiedTracks
import com.ile.syrin_x.utils.toSpotifyUnifiedTracks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    private val getPlaylistByIdSoundCloudUseCase: GetPlaylistByIdSoundCloudUseCase,
    private val getPlaylistByIdSpotifyUseCase: GetPlaylistByIdSpotifyUseCase
): ViewModel() {

    private val _searchFlow = MutableSharedFlow<Response<Any>>()
    val searchFlow = _searchFlow

    var playlistDetails: UnifiedPlaylist? = null
    var songsInPlaylist: List<UnifiedTrack> = mutableListOf()

    fun getPlaylistDetails(playlistId: String, musicSource: MusicSource) = viewModelScope.launch {
        _searchFlow.emit(Response.Loading)

        when(musicSource) {
            MusicSource.SPOTIFY -> {
                getPlaylistByIdSpotifyUseCase(playlistId, GlobalContext.Tokens.spotifyToken).collect { response ->
                    when(response) {
                        is Response.Error -> {
                            _searchFlow.emit(Response.Error(response.message))
                            Log.d("Playlist Details Error", response.message)
                        }
                        Response.Loading -> { }
                        is Response.Success<*> -> {
                            val spotifyResponse = response.data as? SpotifyPlaylistById
                            songsInPlaylist = spotifyResponse?.tracks?.items?.toSpotifyPlaylistByIdTrackItemToUnifiedTracks()!!
                            playlistDetails = fromSpotifyPlaylistToUnifiedPlaylist(spotifyResponse)

                            _searchFlow.emit(Response.Success(true))
                        }
                    }

                }

            }

            MusicSource.SOUNDCLOUD -> {
                getPlaylistByIdSoundCloudUseCase(playlistId, GlobalContext.Tokens.soundCloudToken).collect { response ->
                    when(response) {
                        is Response.Error -> {
                            _searchFlow.emit(Response.Error(response.message))
                            Log.d("Playlist Details Error", response.message)
                        }
                        is Response.Loading -> { }
                        is Response.Success<*> -> {
                            val soundCloudResponse = response.data as? SoundCloudPlaylistById
                            songsInPlaylist = soundCloudResponse?.tracks?.toSoundCloudPlaylistItemUnifiedTracks()!!
                            playlistDetails = fromSoundCloudPlaylistToUnifiedPlaylist(soundCloudResponse)

                            _searchFlow.emit(Response.Success(true))
                        }
                    }
                }
            }

            MusicSource.NOT_SPECIFIED -> {
                
            }
        }
    }

}