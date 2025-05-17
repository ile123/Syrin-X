package com.ile.syrin_x.viewModel

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ile.syrin_x.data.model.entity.SoundCloudUserToken
import com.ile.syrin_x.data.model.entity.SpotifyUserToken
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.DeleteSoundCloudUserTokenUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.ExchangeSoundCloudCodeForTokenUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.GetSoundCloudUserTokenUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.DeleteSpotifyUserTokenUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.ExchangeSpotifyCodeForTokenUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.GetSpotifyUserTokenUseCase
import com.ile.syrin_x.utils.EnvLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSourceViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val exchangeSpotifyCodeForTokenUseCase: ExchangeSpotifyCodeForTokenUseCase,
    private val exchangeSoundCloudCodeForTokenUseCase: ExchangeSoundCloudCodeForTokenUseCase,
    private val spotifyGetUserTokenUseCase: GetSpotifyUserTokenUseCase,
    private val getSoundCloudUserTokenUseCase: GetSoundCloudUserTokenUseCase,
    private val deleteSoundCloudUserTokenUseCase: DeleteSoundCloudUserTokenUseCase,
    private val deleteSpotifyUserTokenUseCase: DeleteSpotifyUserTokenUseCase

): ViewModel() {

    private val _spotifyUserToken = MutableStateFlow<SpotifyUserToken?>(null)
    val spotifyUserToken: StateFlow<SpotifyUserToken?> = _spotifyUserToken

    private val _soundCloudUserToken = MutableStateFlow<SoundCloudUserToken?>(null)
    val soundCloudUserToken: StateFlow<SoundCloudUserToken?> = _soundCloudUserToken

    fun loginSpotify(code: String, redirectUri: String) {
        val authHeader = generateAuthHeader(EnvLoader.spotifyClientId, EnvLoader.spotifyClientSecret)
        viewModelScope.launch {
            getUserUidUseCase.invoke().collect { userUuid ->
                try {
                    val token = exchangeSpotifyCodeForTokenUseCase(userUuid, authHeader, code, redirectUri)
                    _spotifyUserToken.value = token
                } catch (e: Exception) {
                    Log.d("Login Error:", e.message.toString())
                }
            }
        }
    }

    fun loginSoundCloud(code: String, redirectUri: String) {
        viewModelScope.launch {
            getUserUidUseCase.invoke().collect { userUuid ->
                try {
                    val token = exchangeSoundCloudCodeForTokenUseCase(userUuid, EnvLoader.soundCloudClientId, EnvLoader.soundCloudClientSecret, code, redirectUri)
                    _soundCloudUserToken.value = token
                } catch (e: Exception) {
                    Log.d("Login Error:", e.message.toString())
                }
            }
        }
    }

    fun getUserSpotifyToken() {
        viewModelScope.launch {
            if(spotifyUserToken.value == null) {
                getUserUidUseCase.invoke().collect { userUuid ->
                    try {
                        val token = spotifyGetUserTokenUseCase(userUuid)
                        Log.d("Token", token.toString())
                        _spotifyUserToken.value = token

                    } catch (e: Exception) {
                        Log.d("Token fetching error:", e.message.toString())
                    }
                }
            }
        }
    }

    fun getUserSoundCloudToken() {
        viewModelScope.launch {
            if(soundCloudUserToken.value == null) {
                getUserUidUseCase.invoke().collect { userUuid ->
                    try {
                        val token = getSoundCloudUserTokenUseCase(userUuid)
                        _soundCloudUserToken.value = token
                    } catch (e: Exception) {
                        Log.d("Token fetching error:", e.message.toString())
                    }
                }
            }
        }
    }

    fun deleteSpotifyToken() = viewModelScope.launch {
        getUserUidUseCase.invoke().collect { userUuid ->
            deleteSpotifyUserTokenUseCase(userUuid)
        }
    }

    fun deleteSoundCloudToken() = viewModelScope.launch {
        getUserUidUseCase.invoke().collect { userUuid ->
            deleteSoundCloudUserTokenUseCase(userUuid)
        }
    }

    private fun generateAuthHeader(clientId: String, clientSecret: String): String {
        val credentials = "$clientId:$clientSecret"
        return "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }


}