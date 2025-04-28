package com.ile.syrin_x.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ile.syrin_x.data.model.entity.PlaylistWithTracks
import com.ile.syrin_x.data.model.entity.UserCreatedPlaylistEntity
import com.ile.syrin_x.data.model.entity.UserCreatedPlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCreatedPlaylistDao {

    @Transaction
    @Query("SELECT * FROM usercreatedplaylistentity WHERE userId = :userId")
    fun getAllForUserWithTracks(userId: String): Flow<List<PlaylistWithTracks>>

    @Transaction
    @Query("SELECT * FROM usercreatedplaylistentity WHERE userCreatedPlaylistId = :playlistId")
    fun getByIdWithTracks(playlistId: String): Flow<PlaylistWithTracks?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: UserCreatedPlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: UserCreatedPlaylistTrackEntity)

    @Update
    suspend fun updatePlaylist(playlist: UserCreatedPlaylistEntity)

    @Delete
    suspend fun deletePlaylist(playlist: UserCreatedPlaylistEntity)

    @Delete
    suspend fun deleteTrack(track: UserCreatedPlaylistTrackEntity)
}