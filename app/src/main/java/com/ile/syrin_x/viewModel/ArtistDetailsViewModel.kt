package com.ile.syrin_x.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.UnifiedUser
import com.ile.syrin_x.data.model.soundcloud.SoundCloudTracksByUserResponse
import com.ile.syrin_x.data.model.soundcloud.SoundCloudUser
import com.ile.syrin_x.data.model.spotify.SpotifyArtist
import com.ile.syrin_x.data.model.spotify.SpotifyArtistSongsResponse
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.GetSoundCloudArtistsSongsUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.GetSoundCloudUserByIdUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.GetSpotifyArtistByIdUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.GetSpotifyArtistSongsUseCase
import com.ile.syrin_x.utils.GlobalContext
import com.ile.syrin_x.utils.fallbackImageUrl
import com.ile.syrin_x.utils.fromSoundCloudUserTracksToUnifiedTrackList
import com.ile.syrin_x.utils.fromSpotifyArtistTracksToUnifiedTrackList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    private val getSpotifyArtistSongsUseCase: GetSpotifyArtistSongsUseCase,
    private val getSoundCloudArtistsSongsUseCase: GetSoundCloudArtistsSongsUseCase,
    private val getSoundCloudUserByIdUseCase: GetSoundCloudUserByIdUseCase,
    private val getSpotifyUserByIdUseCase: GetSpotifyArtistByIdUseCase
) : ViewModel() {

    private val _searchFlow = MutableSharedFlow<Response<Any>>()
    val searchFlow: SharedFlow<Response<Any>> = _searchFlow

    private var userDetails: UnifiedUser? = null
    var tracksInAlbum = mutableStateListOf<UnifiedTrack>()

    fun getArtistInfoAndSongs(artistId: String, musicSource: MusicSource) = viewModelScope.launch {
        tracksInAlbum.clear()
        _searchFlow.emit(Response.Loading)

        when (musicSource) {
            MusicSource.SPOTIFY -> {
                combine(
                    getSpotifyArtistSongsUseCase(artistId, 0, 0, GlobalContext.Tokens.spotifyToken),
                    getSpotifyUserByIdUseCase(artistId, GlobalContext.Tokens.spotifyToken)
                ) { songsResponse, infoResponse ->
                    when {
                        infoResponse is Response.Success<*> &&
                                songsResponse is Response.Success<*> -> {
                            val spotifyArtist = infoResponse.data as SpotifyArtist
                            userDetails = spotifyArtist.id?.let {
                                UnifiedUser(
                                    id = it,
                                    name = spotifyArtist.name,
                                    avatarUrl = fallbackImageUrl,
                                    permalinkUrl = spotifyArtist.uri,
                                    type = "Artist",
                                    musicSource = MusicSource.SPOTIFY
                                )
                            }
                            val tracks = (songsResponse.data as SpotifyArtistSongsResponse)
                            tracksInAlbum.addAll(
                                tracks.tracks.fromSpotifyArtistTracksToUnifiedTrackList()
                            )
                            Response.Success(userDetails!!)
                        }

                        songsResponse is Response.Error -> songsResponse
                        infoResponse is Response.Error -> infoResponse
                        else -> Response.Loading
                    }
                }
                    .collect { response ->
                        _searchFlow.emit(response)
                    }
            }

            MusicSource.SOUNDCLOUD -> {
                combine(
                    getSoundCloudArtistsSongsUseCase(
                        artistId,
                        0,
                        0,
                        GlobalContext.Tokens.soundCloudToken
                    ),
                    getSoundCloudUserByIdUseCase(artistId, GlobalContext.Tokens.soundCloudToken)
                ) { songsResponse, infoResponse ->
                    when {
                        infoResponse is Response.Success<*> &&
                                songsResponse is Response.Success<*> -> {
                            val scUser = infoResponse.data as SoundCloudUser
                            userDetails = UnifiedUser(
                                id = scUser.id.toString(),
                                name = scUser.full_name,
                                avatarUrl = scUser.avatar_url,
                                permalinkUrl = scUser.permalink_url,
                                type = "User",
                                musicSource = MusicSource.SOUNDCLOUD
                            )
                            val tracks =
                                (songsResponse.data as SoundCloudTracksByUserResponse).collection
                            tracksInAlbum.addAll(
                                tracks.fromSoundCloudUserTracksToUnifiedTrackList()
                            )
                            Response.Success(userDetails!!)
                        }

                        songsResponse is Response.Error -> songsResponse
                        infoResponse is Response.Error -> infoResponse
                        else -> Response.Loading
                    }
                }
                    .collect { response ->
                        _searchFlow.emit(response)
                    }
            }

            MusicSource.NOT_SPECIFIED -> {}
        }
    }
}