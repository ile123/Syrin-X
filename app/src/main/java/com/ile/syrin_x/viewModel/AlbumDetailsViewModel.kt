package com.ile.syrin_x.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.enums.MusicSource
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.spotify.SpotifyAlbum
import com.ile.syrin_x.data.model.spotify.SpotifyAlbumByIdResponse
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.musicsource.spotify.GetAlbumByIdSpotifyUseCase
import com.ile.syrin_x.utils.GlobalContext
import com.ile.syrin_x.utils.toSpotifyUnifiedTracks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val getAlbumByIdSpotifyUseCase: GetAlbumByIdSpotifyUseCase
): ViewModel() {

    private val _searchFlow = MutableSharedFlow<Response<Any>>()
    val searchFlow = _searchFlow

    var albumDetails: SpotifyAlbumByIdResponse? = null
    var tracksInAlbum: List<UnifiedTrack> = mutableListOf()

    fun getAlbumDetails(id: String, musicSource: MusicSource) = viewModelScope.launch {
        _searchFlow.emit(Response.Loading)

        getAlbumByIdSpotifyUseCase(id, GlobalContext.Tokens.spotifyToken).collect { response ->
            when(response) {
                is Response.Error -> {
                    _searchFlow.emit(Response.Error(response.message))
                    Log.d("Playlist Details Error", response.message)
                }
                Response.Loading -> { }
                is Response.Success<*> -> {
                    val spotifyResponse = response.data as? SpotifyAlbumByIdResponse
                    albumDetails = spotifyResponse
                    tracksInAlbum = spotifyResponse?.tracks?.items?.toSpotifyUnifiedTracks()!!

                    _searchFlow.emit(Response.Success(true))
                }
            }
        }
    }

}