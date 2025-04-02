package com.ile.syrin_x.viewModel

import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.model.soundcloud.SoundCloudUserToken
import com.ile.syrin_x.data.model.spotify.SpotifyUserToken
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.ExchangeSoundCloudCodeForTokenUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.GetSoundCloudUserTokenUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.ExchangeSpotifyCodeForTokenUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.GetSpotifyUserTokenUseCase
import com.ile.syrin_x.utils.EnvLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSourceViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val exchangeSpotifyCodeForTokenUseCase: ExchangeSpotifyCodeForTokenUseCase,
    private val exchangeSoundCloudCodeForTokenUseCase: ExchangeSoundCloudCodeForTokenUseCase,
    private val spotifyGetUserTokenUseCase: GetSpotifyUserTokenUseCase,
    private val getSoundCloudUserTokenUseCase: GetSoundCloudUserTokenUseCase

): ViewModel() {

    val spotifyUserToken = mutableStateOf<SpotifyUserToken?>(null)
    val soundCloudUserToken = mutableStateOf<SoundCloudUserToken?>(null)

    fun loginSpotify(code: String, redirectUri: String) {
        val authHeader = generateAuthHeader(EnvLoader.spotifyClientId, EnvLoader.spotifyClientSecret)
        viewModelScope.launch {
            getUserUidUseCase.invoke().collect { userUuid ->
                try {
                    val token = exchangeSpotifyCodeForTokenUseCase(userUuid, authHeader, code, redirectUri)
                    spotifyUserToken.value = token
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
                    soundCloudUserToken.value = token
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
                        spotifyUserToken.value = token

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
                        soundCloudUserToken.value = token
                    } catch (e: Exception) {
                        Log.d("Token fetching error:", e.message.toString())
                    }
                }
            }
        }
    }

    private fun generateAuthHeader(clientId: String, clientSecret: String): String {
        val credentials = "$clientId:$clientSecret"
        return "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }


}