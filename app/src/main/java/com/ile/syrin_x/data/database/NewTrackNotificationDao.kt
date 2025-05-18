package com.ile.syrin_x.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ile.syrin_x.data.model.entity.NewReleaseNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewReleaseNotificationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: NewReleaseNotificationEntity)

    @Query("SELECT * FROM new_releases WHERE userId = :userId AND trackId = :trackId LIMIT 1")
    suspend fun getExistingNotification(userId: String, trackId: Long): NewReleaseNotificationEntity?

    @Query("SELECT * FROM new_releases WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllForUser(userId: String): Flow<List<NewReleaseNotificationEntity>>

    @Query("UPDATE new_releases SET seen = 1 WHERE userId = :userId AND trackId = :trackId")
    suspend fun markSeen(userId: String, trackId: Long)
}