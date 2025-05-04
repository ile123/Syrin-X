package com.ile.syrin_x.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ile.syrin_x.data.model.entity.FavoriteArtistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteArtistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteArtistEntity: FavoriteArtistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(favoriteArtistEntities: List<FavoriteArtistEntity>)

    @Delete
    suspend fun delete(favoriteArtistEntity: FavoriteArtistEntity)

    @Query("DELETE FROM FavoriteArtistEntity WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: String)

    @Query("SELECT * FROM FavoriteArtistEntity WHERE id = :id AND userId = :userId")
    suspend fun getById(id: String, userId: String): FavoriteArtistEntity?

    @Query("SELECT * FROM FavoriteArtistEntity WHERE userId = :userId")
    fun getAllForUser(userId: String): Flow<List<FavoriteArtistEntity>>
}