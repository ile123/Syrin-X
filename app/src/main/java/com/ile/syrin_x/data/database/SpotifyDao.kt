package com.ile.syrin_x.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ile.syrin_x.data.model.entity.SpotifyUserToken

@Dao
interface SpotifyDao {
    @Query("SELECT * FROM spotifyusertoken WHERE userId = :userId LIMIT 1")
    fun getUserToken(userId: String): SpotifyUserToken?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: SpotifyUserToken)

    @Query("DELETE FROM spotifyusertoken WHERE userId = :userId")
    suspend fun deleteUser(userId: String)

    @Query("UPDATE spotifyusertoken SET accessToken = :accessToken, expiresAt = :expiresIn WHERE userId = :userId")
    suspend fun updateToken(userId: String, accessToken: String, expiresIn: Long)
}
