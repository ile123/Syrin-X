package com.ile.syrin_x.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ile.syrin_x.data.model.entity.PreviouslyPlayedTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreviouslyPlayedTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(previouslyPlayedTrackEntity: PreviouslyPlayedTrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(previouslyPlayedTrackEntities: List<PreviouslyPlayedTrackEntity>)

    @Update
    suspend fun update(previouslyPlayedTrackEntity: PreviouslyPlayedTrackEntity)

    @Delete
    suspend fun delete(previouslyPlayedTrackEntity: PreviouslyPlayedTrackEntity)

    @Query("DELETE FROM PreviouslyPlayedTrackEntity WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM PreviouslyPlayedTrackEntity WHERE trackId = :id")
    suspend fun getById(id: String): PreviouslyPlayedTrackEntity?

    @Query("SELECT * FROM PreviouslyPlayedTrackEntity WHERE userId = :userId")
    fun getAllForUser(userId: String): Flow<List<PreviouslyPlayedTrackEntity>>
}