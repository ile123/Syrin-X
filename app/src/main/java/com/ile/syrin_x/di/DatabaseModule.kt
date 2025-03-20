package com.ile.syrin_x.di

import android.content.Context
import androidx.room.Room
import com.ile.syrin_x.data.database.AppDatabase
import com.ile.syrin_x.data.database.SoundCloudDao
import com.ile.syrin_x.data.database.SpotifyDao
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
}
