package com.ile.syrin_x.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ile.syrin_x.data.model.entity.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface FavoriteTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteTrackEntity: FavoriteTrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(favoriteTrackEntities: List<FavoriteTrackEntity>)

    @Delete
    suspend fun delete(favoriteTrackEntity: FavoriteTrackEntity)

    @Query("DELETE FROM FavoriteTrackEntity WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM FavoriteTrackEntity WHERE favoriteTrackId = :id")
    suspend fun getById(id: String): FavoriteTrackEntity?

    @Query("SELECT * FROM FavoriteTrackEntity WHERE userId = :userId")
    fun getAllForUser(userId: String): Flow<List<FavoriteTrackEntity>>
}