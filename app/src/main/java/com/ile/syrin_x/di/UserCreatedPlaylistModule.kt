package com.ile.syrin_x.di

import com.google.firebase.database.FirebaseDatabase
import com.ile.syrin_x.data.database.UserCreatedPlaylistDao
import com.ile.syrin_x.data.database.UserCreatedPlaylistTrackDao
import com.ile.syrin_x.data.repository.usercreatedplaylist.UserCreatedPlaylistRepositoryImpl
import com.ile.syrin_x.domain.repository.UserCreatedPlaylistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserCreatedPlaylistModule {

    @Provides
    @Singleton
    fun provideUserCreatedPlaylistModule(
        playlistDao: UserCreatedPlaylistDao,
        trackDao: UserCreatedPlaylistTrackDao,
        db: FirebaseDatabase
    ): UserCreatedPlaylistRepository = UserCreatedPlaylistRepositoryImpl(playlistDao, trackDao, db)

}