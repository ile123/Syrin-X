package com.ile.syrin_x.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ile.syrin_x.data.model.entity.NewReleaseNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NewReleaseNotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NewReleaseNotificationEntity)

    @Query("SELECT * FROM new_releases WHERE title = :title AND artistId = :artistId LIMIT 1")
    suspend fun getExistingNotification(
        title: String,
        artistId: String
    ): NewReleaseNotificationEntity?

    @Query("SELECT * FROM new_releases WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllForUser(userId: String): List<NewReleaseNotificationEntity>

    @Query("UPDATE new_releases SET seen = 1 WHERE id = :notificationId")
    suspend fun markSeen(notificationId: String)
}