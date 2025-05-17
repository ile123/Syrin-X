package com.ile.syrin_x.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.enums.UserCreatedPlaylistTrackAction
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.UnifiedUser
import com.ile.syrin_x.data.model.usercreatedplaylist.FavoriteTrack
import com.ile.syrin_x.data.model.usercreatedplaylist.UserCreatedPlaylist
import com.ile.syrin_x.domain.core.Response
import com.ile.syrin_x.domain.usecase.auth.GetUserUidUseCase
import com.ile.syrin_x.domain.usecase.user.AddOrRemoveTrackFromFavoritesUseCase
import com.ile.syrin_x.domain.usecase.usercreatedplaylist.AddOrRemoveUserCreatedPlaylistTrackUseCase
import com.ile.syrin_x.domain.usecase.usercreatedplaylist.CreateUserCreatedPlaylistUseCase
import com.ile.syrin_x.domain.usecase.usercreatedplaylist.DeleteUserCreatedPlaylistUseCase
import com.ile.syrin_x.domain.usecase.user.GetAllFavoriteTracksByUserUseCase
import com.ile.syrin_x.domain.usecase.usercreatedplaylist.GetAllUserCreatedPlaylistsByUserUseCase
import com.ile.syrin_x.domain.usecase.user.GetFavoriteTrackByIdUseCase
import com.ile.syrin_x.domain.usecase.usercreatedplaylist.GetUserCreatedPlaylistByIdUseCase
import com.ile.syrin_x.domain.usecase.usercreatedplaylist.UpdateUserCreatedPlaylistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistManagementViewModel @Inject constructor(
    private val getUserUidUseCase: GetUserUidUseCase,
    private val addOrRemoveTrackFromFavoritesUseCase: AddOrRemoveTrackFromFavoritesUseCase,
    private val addOrRemoveUserCreatedPlaylistTrackUseCase: AddOrRemoveUserCreatedPlaylistTrackUseCase,
    private val createUserCreatedPlaylistUseCase: CreateUserCreatedPlaylistUseCase,
    private val deleteUserCreatedPlaylistUseCase: DeleteUserCreatedPlaylistUseCase,
    private val getAllFavoriteTracksByUserUseCase: GetAllFavoriteTracksByUserUseCase,
    private val getAllUserCreatedPlaylistsByUserUseCase: GetAllUserCreatedPlaylistsByUserUseCase,
    private val getFavoriteTrackByIdUseCase: GetFavoriteTrackByIdUseCase,
    private val getUserCreatedPlaylistByIdUseCase: GetUserCreatedPlaylistByIdUseCase,
    private val updateUserCreatedPlaylistUseCase: UpdateUserCreatedPlaylistUseCase
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<UserCreatedPlaylist>>(emptyList())
    val userPlaylists: StateFlow<List<UserCreatedPlaylist>> = _playlists.asStateFlow()

    private val _favorites = MutableStateFlow<List<FavoriteTrack>>(emptyList())
    val favorites: StateFlow<List<FavoriteTrack>> = _favorites.asStateFlow()

    private val _dataFlow = MutableSharedFlow<Response<Any>>()
    val dataFlow = _dataFlow

    fun getAllUserFavoriteTracks() = viewModelScope.launch {
        val userUuid = getUserUidUseCase.invoke().first()
        getAllFavoriteTracksByUserUseCase(userUuid).collect { response ->
            when (response) {
                is Response.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    _favorites.value = response.data as List<FavoriteTrack>
                }
                is Response.Error -> {
                    _dataFlow.emit(Response.Error(response.message))
                }
                is Response.Loading -> {  }
            }
        }
    }

    fun addOrRemoveTrackFromFavorites(track: UnifiedTrack) = viewModelScope.launch {
        val userUuid = getUserUidUseCase.invoke().first()
        addOrRemoveTrackFromFavoritesUseCase(track, userUuid).collect { response ->
            if (response is Response.Success<*>) {
                getAllUserFavoriteTracks()
            } else if (response is Response.Error) {
                _dataFlow.emit(Response.Error(response.message))
            }
        }
    }

    fun addOrRemoveTrackFromPlaylist(
        track: UnifiedTrack,
        playlistId: String,
        action: UserCreatedPlaylistTrackAction
    ) = viewModelScope.launch {
        addOrRemoveUserCreatedPlaylistTrackUseCase(track, playlistId, action).collect { response ->
            when (response) {
                is Response.Success<*> -> {
                    val updated = response.data as UserCreatedPlaylist
                    _playlists.update { list ->
                        list.map { if (it.userCreatedPlaylistId == updated.userCreatedPlaylistId) updated else it }
                    }
                }
                is Response.Error -> {
                    _dataFlow.emit(Response.Error(response.message))
                }
                is Response.Loading -> { }
            }
        }
    }

    fun createPlaylist(name: String) = viewModelScope.launch {
        val userUuid = getUserUidUseCase.invoke().first()
        createUserCreatedPlaylistUseCase(name, userUuid).collect { response ->
            when (response) {
                is Response.Success<*> -> {
                    @Suppress("UNCHECKED_CAST")
                    _playlists.value = response.data as List<UserCreatedPlaylist>
                }
                is Response.Error -> {
                    _dataFlow.emit(Response.Error(response.message))
                }
                is Response.Loading -> { }
            }
        }
    }

    fun deletePlaylist(playlistId: String) = viewModelScope.launch {
        deleteUserCreatedPlaylistUseCase(playlistId).collect { response ->
            when (response) {
                is Response.Success<*> -> {
                    val removed = response.data as UserCreatedPlaylist
                    _playlists.update { list ->
                        list.filterNot { it.userCreatedPlaylistId == removed.userCreatedPlaylistId }
                    }
                }
                is Response.Error -> {
                    _dataFlow.emit(Response.Error(response.message))
                }
                is Response.Loading -> { }
            }
        }
    }

    fun getAllUsersPlaylists() = viewModelScope.launch {
        val userUuid = getUserUidUseCase.invoke().first()
        getAllUserCreatedPlaylistsByUserUseCase(userUuid).collect { response ->
            if (response is Response.Success<*>) {
                @Suppress("UNCHECKED_CAST")
                _playlists.value = response.data as List<UserCreatedPlaylist>
            } else if (response is Response.Error) {
                _dataFlow.emit(Response.Error(response.message))
            }
        }
    }

    fun getFavoriteTrackById(favoriteTrackId: String) = viewModelScope.launch {
        getFavoriteTrackByIdUseCase(favoriteTrackId).collect { response ->
            if (response is Response.Error) {
                _dataFlow.emit(Response.Error(response.message))
            }
        }
    }

    fun getUserPlaylistById(playlistId: String) = viewModelScope.launch {
        getUserCreatedPlaylistByIdUseCase(playlistId).collect { response ->
            if (response is Response.Success<*>) {
                _dataFlow.emit(Response.Success(response.data as UserCreatedPlaylist))
            } else if (response is Response.Error) {
                _dataFlow.emit(Response.Error(response.message))
            }
        }
    }

    fun updateUserPlaylist(playlistId: String, newName: String) = viewModelScope.launch {
        updateUserCreatedPlaylistUseCase(newName, playlistId).collect { response ->
            when (response) {
                is Response.Success<*> -> {
                    val updated = response.data as UserCreatedPlaylist
                    _playlists.update { list ->
                        list.map { if (it.userCreatedPlaylistId == updated.userCreatedPlaylistId) updated else it }
                    }
                }
                is Response.Error -> {
                    _dataFlow.emit(Response.Error(response.message))
                }
                is Response.Loading -> { }
            }
        }
    }
}