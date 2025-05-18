package com.ile.syrin_x.di

import android.content.Context
import androidx.room.Room
import com.ile.syrin_x.data.database.AppDatabase
import com.ile.syrin_x.data.database.FavoriteArtistDao
import com.ile.syrin_x.data.database.FavoriteTrackDao
import com.ile.syrin_x.data.database.NewReleaseNotificationDao
import com.ile.syrin_x.data.database.PreviouslyPlayedTrackDao
import com.ile.syrin_x.data.database.SoundCloudDao
import com.ile.syrin_x.data.database.SpotifyDao
import com.ile.syrin_x.data.database.UserCreatedPlaylistDao
import com.ile.syrin_x.data.database.UserCreatedPlaylistTrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "syrinx_db"
        )
            .setQueryExecutor(Executors.newSingleThreadExecutor())
            .setTransactionExecutor(Executors.newFixedThreadPool(2))
            .build()
    }

    @Provides
    @Singleton
    fun provideSpotifyDao(db: AppDatabase): SpotifyDao = db.spotifyDao()

    @Provides
    @Singleton
    fun provideSoundCloudTokenDao(db: AppDatabase): SoundCloudDao = db.soundCloudDao()

    @Provides
    @Singleton
    fun providesUserCreatedPlaylistDao(db: AppDatabase): UserCreatedPlaylistDao = db.userCreatedPlaylistDao()

    @Provides
    @Singleton
    fun providesUserCreatedPlaylistTrackDao(db: AppDatabase): UserCreatedPlaylistTrackDao = db.userCreatedPlaylistTrackDao()

    @Provides
    @Singleton
    fun providesFavoriteTrackDao(db: AppDatabase): FavoriteTrackDao = db.favoriteTrackDao()

    @Provides
    @Singleton
    fun providesPreviouslyPlayedTrackDao(db: AppDatabase): PreviouslyPlayedTrackDao = db.previouslyPlayedTrackDao()

    @Provides
    @Singleton
    fun providesFavoriteArtistDao(db: AppDatabase): FavoriteArtistDao = db.favoriteArtistDao()

    @Provides
    @Singleton
    fun providesNewReleaseNotificationDao(db: AppDatabase): NewReleaseNotificationDao = db.newReleasesNotificationDao()
}
