package com.ile.syrin_x.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.model.UserInfo
import com.ile.syrin_x.data.model.entity.FavoriteArtist
import com.ile.syrin_x.data.model.entity.PreviouslyPlayedTrack
import com.ile.syrin_x.data.model.usercreatedplaylist.FavoriteTrack
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.user.ChangeUserProfileUseCase
import com.ile.syrin_x.domain.usecase.user.GetAllFavoriteArtistsByUserUseCase
import com.ile.syrin_x.domain.usecase.user.GetAllFavoriteTracksByUserUseCase
import com.ile.syrin_x.domain.usecase.user.GetAllPreviouslyPlayedTracksByUserUseCase
import com.ile.syrin_x.domain.usecase.user.GetUserInfoUseCase
import com.ile.syrin_x.domain.usecase.user.UpdateUserInfoUseCase
import com.ile.syrin_x.domain.usecase.user.UploadProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel  @Inject constructor(
    private val getAllFavoriteTracksByUserUseCase: GetAllFavoriteTracksByUserUseCase,
    private val getAllFavoriteArtistsByUserUseCase: GetAllFavoriteArtistsByUserUseCase,
    private val getAllPreviouslyPlayedTracksByUserUseCase: GetAllPreviouslyPlayedTracksByUserUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase,
    private val getUserUidUseCase: GetUserUidUseCase
    ): ViewModel() {

        private val _profileFlow = MutableSharedFlow<Response<Any>>()
        val profileFlow = _profileFlow

        val userInfo = mutableStateOf(UserInfo("", "", "", ""))
        val userProfileImage = mutableStateOf("")
        val previouslyPlayedTracks = mutableStateListOf<PreviouslyPlayedTrack>()
        val favoriteTracks = mutableStateListOf<FavoriteTrack>()
        val favoriteArtists = mutableStateListOf<FavoriteArtist>()

    fun getUserInfo() = viewModelScope.launch {
        getUserUidUseCase.invoke().collect { userUid ->
            combine(getUserInfoUseCase(userUid), getAllFavoriteArtistsByUserUseCase(userUid), getAllFavoriteTracksByUserUseCase(userUid), getAllPreviouslyPlayedTracksByUserUseCase(userUid)) {
                userInfoResponse, favoriteArtistsResponse, favoriteTracksResponse, previouslyPlayedTracksResponse ->
                when {
                            userInfoResponse is Response.Success<*> &&
                            favoriteTracksResponse is Response.Success<*> &&
                            favoriteArtistsResponse is Response.Success<*> &&
                                    previouslyPlayedTracksResponse is Response.Success<*> -> {
                                val userInfoData = userInfoResponse.data as? UserInfo
                                val favoriteTracksData = favoriteTracksResponse.data as? List<FavoriteTrack>
                                val favoriteArtistsData = favoriteArtistsResponse.data as? List<FavoriteArtist>
                                val previouslyPlayedTracksData = previouslyPlayedTracksResponse.data as? List<PreviouslyPlayedTrack>

                                favoriteTracks.clear()
                                favoriteArtists.clear()
                                previouslyPlayedTracks.clear()

                                userInfoData?.let {
                                    userInfo.value = userInfoData
                                }

                                favoriteTracksData?.let {
                                    favoriteTracksData.forEach {
                                        if(favoriteTracks.find { x -> x.trackId == it.trackId } == null) {
                                            favoriteTracks.add(it)
                                        }
                                    }
                                }

                                favoriteArtistsData?.let {
                                    favoriteArtistsData.forEach {
                                        if(favoriteArtists.find { x -> x.id == it.id } == null) {
                                            favoriteArtists.add(it)
                                        }
                                    }
                                }

                                previouslyPlayedTracksData?.let {
                                    previouslyPlayedTracksData.forEach {
                                        if(previouslyPlayedTracks.find { x -> x.trackId == it.trackId} == null) {
                                            previouslyPlayedTracks.add(it)
                                        }
                                    }
                                }

                                Response.Success(true)
                            }
                    favoriteTracksResponse is Response.Error -> favoriteTracksResponse
                    favoriteArtistsResponse is Response.Error -> favoriteArtistsResponse
                    previouslyPlayedTracksResponse is Response.Error -> previouslyPlayedTracksResponse
                    else -> Response.Loading
                }
            }.collect { response ->
                _profileFlow.emit(response)
            }
        }
    }

    fun updateUserInfo(userName: String, fullName: String) = viewModelScope.launch {
        getUserUidUseCase.invoke().collect { userUid ->
            updateUserInfoUseCase(userUid, userName, fullName).collect { response ->
                when(response) {
                    is Response.Error -> {
                        Log.d("ProfileViewModel", response.message)
                    }
                    is Response.Loading -> {  }
                    is Response.Success<*> -> {
                        val updatedUserInfoData = response.data as UserInfo
                        userInfo.value = updatedUserInfoData
                    }
                }
            }
        }
    }

    fun changeUserProfile(imageUri: Uri?, context: Context) = viewModelScope.launch {
        getUserUidUseCase.invoke().collect { userUid ->
            imageUri?.let {
                when(val response = uploadProfileImageUseCase(userUid, imageUri, context)) {
                    is Response.Error -> {
                        Log.d("ProfileViewModel", response.message)
                    }
                    is Response.Loading -> { }
                    is Response.Success<*> -> {
                        val newProfileImageData = response.data as? UserInfo
                        newProfileImageData?.profilePicture?.let {
                            userProfileImage.value = newProfileImageData.profilePicture
                        }
                    }
                }
            }
        }
    }
}