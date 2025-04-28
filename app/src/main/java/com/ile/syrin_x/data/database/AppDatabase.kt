package com.ile.syrin_x.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ile.syrin_x.data.converters.Converters
import com.ile.syrin_x.data.model.entity.FavoriteArtistEntity
import com.ile.syrin_x.data.model.entity.FavoriteTrackEntity
import com.ile.syrin_x.data.model.entity.PreviouslyPlayedTrackEntity
import com.ile.syrin_x.data.model.entity.SpotifyUserToken
import com.ile.syrin_x.data.model.entity.SoundCloudUserToken
import com.ile.syrin_x.data.model.entity.UserCreatedPlaylistEntity
import com.ile.syrin_x.data.model.entity.UserCreatedPlaylistTrackEntity

@Database(
    entities = [
        SpotifyUserToken::class,
        SoundCloudUserToken::class,
        FavoriteTrackEntity::class,
        UserCreatedPlaylistEntity::class,
        UserCreatedPlaylistTrackEntity::class,
        PreviouslyPlayedTrackEntity::class,
        FavoriteArtistEntity::class
    ],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun spotifyDao(): SpotifyDao
    abstract fun soundCloudDao(): SoundCloudDao
    abstract fun userCreatedPlaylistDao(): UserCreatedPlaylistDao
    abstract fun userCreatedPlaylistTrackDao(): UserCreatedPlaylistTrackDao
    abstract fun favoriteTrackDao(): FavoriteTrackDao
    abstract fun previouslyPlayedTrackDao(): PreviouslyPlayedTrackDao
    abstract fun favoriteArtistDao(): FavoriteArtistDao
}
