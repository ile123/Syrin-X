package com.ile.syrin_x.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.soundcloud.SoundCloudTrackById
import com.ile.syrin_x.data.model.spotify.SpotifyTrackDetails
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.GetTrackByIdSoundCloudUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.GetTrackByIdSpotifyUseCase
import com.ile.syrin_x.utils.GlobalContext
import com.ile.syrin_x.utils.fromSoundCloudTrackToUnifiedTrack
import com.ile.syrin_x.utils.fromSpotifyTrackToUnifiedTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackDetailsViewModel @Inject constructor(
    private val getTrackByIdSoundCloudUseCase: GetTrackByIdSoundCloudUseCase,
    private val getTrackByIdSpotifyUseCase: GetTrackByIdSpotifyUseCase
) : ViewModel() {

    private val _searchFlow = MutableSharedFlow<Response<Any>>()
    val searchFlow = _searchFlow

    var trackDetails: UnifiedTrack? = null

    fun getTrackDetails(trackId: String, musicSource: MusicSource) = viewModelScope.launch {
        _searchFlow.emit(Response.Loading)

        when (musicSource) {
            MusicSource.SPOTIFY -> {
                getTrackByIdSpotifyUseCase(trackId, GlobalContext.Tokens.spotifyToken).collect { response ->
                    when (response) {
                        is Response.Loading -> {
                        }

                        is Response.Error -> {
                            _searchFlow.emit(Response.Error(response.message))
                            Log.d("Track Details Error", response.message)
                        }

                        is Response.Success<*> -> {
                            val spotifyResponse = response.data as? SpotifyTrackDetails
                            trackDetails = fromSpotifyTrackToUnifiedTrack(spotifyResponse)

                            _searchFlow.emit(Response.Success(true))
                        }
                    }
                }
            }
            MusicSource.SOUNDCLOUD -> {
                getTrackByIdSoundCloudUseCase(trackId, GlobalContext.Tokens.soundCloudToken).collect { response ->
                    when (response) {
                        is Response.Loading -> {

                        }

                        is Response.Error -> {
                            _searchFlow.emit(Response.Error(response.message))
                        }

                        is Response.Success<*> -> {
                            val soundCloudResponse = response.data as? SoundCloudTrackById
                            trackDetails = fromSoundCloudTrackToUnifiedTrack(soundCloudResponse)


                            _searchFlow.emit(Response.Success(true))
                        }
                    }
                }
            }
            MusicSource.NOT_SPECIFIED -> {
                _searchFlow.emit(Response.Error("Invalid music source"))
            }
        }
    }
}
