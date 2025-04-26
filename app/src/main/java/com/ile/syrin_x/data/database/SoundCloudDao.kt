package com.ile.syrin_x.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ile.syrin_x.data.model.entity.SoundCloudUserToken

@Dao
interface SoundCloudDao {
    @Query("SELECT * FROM soundcloudusertoken WHERE userId = :userId LIMIT 1")
    suspend fun getToken(userId: String): SoundCloudUserToken?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: SoundCloudUserToken)

    @Query("DELETE FROM soundcloudusertoken WHERE userId = :userId")
    suspend fun deleteToken(userId: String)

    @Query("UPDATE soundcloudusertoken SET accessToken = :accessToken, refreshToken = :refreshToken, expiresAt = :expiresIn WHERE userId = :userId")
    suspend fun updateToken(userId: String, accessToken: String, refreshToken: String?, expiresIn: Long)
}
