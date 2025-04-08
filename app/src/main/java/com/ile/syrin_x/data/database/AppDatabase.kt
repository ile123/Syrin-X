package com.ile.syrin_x.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ile.syrin_x.data.model.spotify.SpotifyUserToken
import com.ile.syrin_x.data.model.soundcloud.SoundCloudUserToken

@Database(entities = [SpotifyUserToken::class, SoundCloudUserToken::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun spotifyDao(): SpotifyDao
    abstract fun soundCloudDao(): SoundCloudDao
}
