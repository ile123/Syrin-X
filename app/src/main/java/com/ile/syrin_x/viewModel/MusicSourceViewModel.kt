package com.ile.syrin_x.viewModel

import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.model.SoundCloudUserToken
import com.ile.syrin_x.data.model.SpotifyUserToken
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.musicsource.soundcloud.SoundCloudLoginUseCase
import com.ile.syrin_x.domain.usecase.musicsource.spotify.SpotifyLoginUseCase
import com.ile.syrin_x.utils.EnvLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicSourceViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val spotifyLoginUseCase: SpotifyLoginUseCase,
    private val soundCloudLoginUseCase: SoundCloudLoginUseCase
): ViewModel() {
    private val _spotifyToken = MutableLiveData<SpotifyUserToken>()
    val spotifyToken: LiveData<SpotifyUserToken> get() = _spotifyToken

    private val _soundCloudToken = MutableLiveData<SoundCloudUserToken>()
    val soundCloudToken: LiveData<SoundCloudUserToken> get() = _soundCloudToken

    fun loginSpotify(code: String, redirectUri: String) {
        val authHeader = generateAuthHeader(EnvLoader.spotifyClientId, EnvLoader.spotifyClientSecret)
        viewModelScope.launch {
            getUserUidUseCase.invoke().collect { userUuid ->
                try {
                    val token = spotifyLoginUseCase(userUuid, authHeader, code, redirectUri)
                    _spotifyToken.value = token
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    fun loginSoundCloud(code: String, redirectUri: String) {
        viewModelScope.launch {
            getUserUidUseCase.invoke().collect { userUuid ->
                try {
                    val token = soundCloudLoginUseCase(userUuid, EnvLoader.soundCloudClientId, EnvLoader.soundCloudClientSecret, code, redirectUri)
                    _soundCloudToken.value = token
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    private fun generateAuthHeader(clientId: String, clientSecret: String): String {
        val credentials = "$clientId:$clientSecret"
        return "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
    }


}