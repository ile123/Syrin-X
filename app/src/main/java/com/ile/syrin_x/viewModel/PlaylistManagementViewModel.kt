package com.ile.syrin_x.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ile.syrin_x.data.enums.UserCreatedPlaylistTrackAction
import com.ile.syrin_x.data.model.UnifiedTrack
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

    val userPlaylists = mutableStateListOf<UserCreatedPlaylist>()
    val userFavoriteTracks = mutableStateListOf<FavoriteTrack>()

    private val _dataFlow = MutableSharedFlow<Response<Any>>()
    val dataFlow = _dataFlow

    fun addOrRemoveTrackFromFavorites(track: UnifiedTrack) = viewModelScope.launch {
        getUserUidUseCase.invoke().collect { userUuid ->
            addOrRemoveTrackFromFavoritesUseCase(track, userUuid).collect { response ->
                when (response) {
                    is Response.Error -> {
                        _dataFlow.emit(Response.Error(response.message))
                        Log.d("Playlist Management Error", response.message)
                    }
                    is Response.Loading -> { /* no-op */ }
                    is Response.Success<*> -> {
                        val favoriteTrack = response.data as FavoriteTrack
                        if (userFavoriteTracks.none { it.favoriteTrackId == track.id }) {
                            userFavoriteTracks.add(favoriteTrack)
                        } else {
                            userFavoriteTracks.removeAll { it.favoriteTrackId == track.id }
                        }
                    }
                }
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
                is Response.Error -> {
                    _dataFlow.emit(Response.Error(response.message))
                    Log.d("Playlist Management Error", response.message)
                }
                is Response.Loading -> { /* no-op */ }
                is Response.Success<*> -> {
                    val updatedPlaylist = response.data as UserCreatedPlaylist
                    val idx = userPlaylists.indexOfFirst { it.userCreatedPlaylistId == updatedPlaylist.userCreatedPlaylistId }
                    if (idx == -1) userPlaylists.add(updatedPlaylist)
                    else userPlaylists[idx] = updatedPlaylist
                }
            }
        }
    }

    fun createPlaylist(name: String) = viewModelScope.launch {
        getUserUidUseCase.invoke().collect { userUuid ->
            createUserCreatedPlaylistUseCase(name, userUuid).collect { response ->
                when (response) {
                    is Response.Error -> {
                        _dataFlow.emit(Response.Error(response.message))
                        Log.d("Playlist Management Error", response.message)
                    }
                    is Response.Loading -> { /* no-op */ }
                    is Response.Success<*> -> {
                        val playlists = response.data as List<UserCreatedPlaylist>
                        userPlaylists.clear()
                        userPlaylists.addAll(playlists)
                    }
                }
            }
        }
    }

    fun deletePlaylist(playlistId: String) = viewModelScope.launch {
        deleteUserCreatedPlaylistUseCase(playlistId).collect { response ->
            when (response) {
                is Response.Error -> {
                    _dataFlow.emit(Response.Error(response.message))
                    Log.d("Playlist Management Error", response.message)
                }
                is Response.Loading -> { /* no-op */ }
                is Response.Success<*> -> {
                    val removed = response.data as UserCreatedPlaylist
                    userPlaylists.removeAll { it.userCreatedPlaylistId == removed.userCreatedPlaylistId }
                }
            }
        }
    }

    fun getAllUserFavoriteTracks() = viewModelScope.launch {
        getUserUidUseCase.invoke().collect { userUuid ->
            getAllFavoriteTracksByUserUseCase(userUuid).collect { response ->
                when (response) {
                    is Response.Error -> {
                        _dataFlow.emit(Response.Error(response.message))
                        Log.d("Playlist Management Error", response.message)
                    }
                    is Response.Loading -> { /* no-op */ }
                    is Response.Success<*> -> {
                        val favorites = response.data as List<FavoriteTrack>
                        userFavoriteTracks.clear()
                        userFavoriteTracks.addAll(favorites)
                    }
                }
            }
        }
    }

    fun getAllUsersPlaylists() = viewModelScope.launch {
        getUserUidUseCase.invoke().collect { userUuid ->
            getAllUserCreatedPlaylistsByUserUseCase(userUuid).collect { response ->
                when (response) {
                    is Response.Error -> {
                        _dataFlow.emit(Response.Error(response.message))
                        Log.d("Playlist Management Error", response.message)
                    }
                    is Response.Loading -> { /* no-op */ }
                    is Response.Success<*> -> {
                        val playlists = response.data as List<UserCreatedPlaylist>
                        userPlaylists.clear()
                        userPlaylists.addAll(playlists)
                    }
                }
            }
        }
    }

    fun getFavoriteTrackById(favoriteTrackId: String) = viewModelScope.launch {
        getFavoriteTrackByIdUseCase(favoriteTrackId).collect { response ->
            when (response) {
                is Response.Error -> {
                    _dataFlow.emit(Response.Error(response.message))
                    Log.d("Playlist Management Error", response.message)
                }
                is Response.Loading -> { /* no-op */ }
                is Response.Success<*> -> {
                    val favorite = response.data as FavoriteTrack
                    // Navigate to track details screen here
                }
            }
        }
    }

    fun getUserPlaylistById(playlistId: String) = viewModelScope.launch {
        getUserCreatedPlaylistByIdUseCase(playlistId).collect { response ->
            when (response) {
                is Response.Error -> {
                    _dataFlow.emit(Response.Error(response.message))
                    Log.d("Playlist Management Error", response.message)
                }
                is Response.Loading -> { /* no-op */ }
                is Response.Success<*> -> {
                    val playlist = response.data as UserCreatedPlaylist
                    _dataFlow.emit(Response.Success(playlist))
                }

                else -> {}
            }
        }
    }

    fun updateUserPlaylist(playlistId: String, newName: String) = viewModelScope.launch {
        updateUserCreatedPlaylistUseCase(newName, playlistId).collect { response ->
            when (response) {
                is Response.Error -> {
                    _dataFlow.emit(Response.Error(response.message))
                    Log.d("Playlist Management Error", response.message)
                }
                is Response.Loading -> { /* no-op */ }
                is Response.Success<*> -> {
                    val updated = response.data as UserCreatedPlaylist
                    val idx = userPlaylists.indexOfFirst { it.userCreatedPlaylistId == updated.userCreatedPlaylistId }
                    if (idx != -1) userPlaylists[idx] = updated
                }
            }
        }
    }
}
