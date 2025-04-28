package com.ile.syrin_x.domain.repository

import com.ile.syrin_x.data.enums.UserCreatedPlaylistTrackAction
import com.ile.syrin_x.data.model.UnifiedTrack
import com.ile.syrin_x.data.model.usercreatedplaylist.UserCreatedPlaylist
import com.ile.syrin_x.data.model.usercreatedplaylist.UserCreatedPlaylistTrack
import com.ile.syrin_x.domain.core.Response
import kotlinx.coroutines.flow.Flow

interface UserCreatedPlaylistRepository {
    suspend fun getAllUserCreatedPlaylistsByUser(userId: String): Flow<Response<List<UserCreatedPlaylist>>>
    suspend fun getUserCreatedPlaylistById(playlistId: String): Flow<Response<UserCreatedPlaylist>>
    suspend fun createUserCreatedPlaylist(name: String, userId: String): Flow<Response<List<UserCreatedPlaylist>>>
    suspend fun updateUserCreatedPlaylist(newName: String, playlistId: String): Flow<Response<UserCreatedPlaylist>>
    suspend fun addOrRemoveUserCreatedPlaylistTrack(track: UnifiedTrack, playlistId: String, action: UserCreatedPlaylistTrackAction): Flow<Response<UserCreatedPlaylist>>
    suspend fun deleteUserCreatedPlaylist(playlistId: String): Flow<Response<UserCreatedPlaylist>>
}