package com.ile.syrin_x.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ile.syrin_x.data.model.entity.UserCreatedPlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserCreatedPlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: UserCreatedPlaylistTrackEntity)

    @Update
    suspend fun updateTrack(track: UserCreatedPlaylistTrackEntity)

    @Delete
    suspend fun deleteTrack(track: UserCreatedPlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks WHERE userCreatedPlaylistTrackId = :id")
    suspend fun getById(id: String): UserCreatedPlaylistTrackEntity?

    @Query("SELECT * FROM playlist_tracks WHERE playlistId = :playlistId")
    fun getAllByPlaylistId(playlistId: String): Flow<List<UserCreatedPlaylistTrackEntity>>
}