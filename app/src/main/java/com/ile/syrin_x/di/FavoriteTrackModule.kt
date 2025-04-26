package com.ile.syrin_x.di

import com.google.firebase.database.FirebaseDatabase
import com.ile.syrin_x.data.database.FavoriteTrackDao
import com.ile.syrin_x.data.database.UserCreatedPlaylistDao
import com.ile.syrin_x.data.database.UserCreatedPlaylistTrackDao
import com.ile.syrin_x.data.repository.usercreatedplaylist.FavoriteTrackRepositoryImpl
import com.ile.syrin_x.data.repository.usercreatedplaylist.UserCreatedPlaylistRepositoryImpl
import com.ile.syrin_x.domain.repository.FavoriteTrackRepository
import com.ile.syrin_x.domain.repository.UserCreatedPlaylistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FavoriteTrackModule {

    @Provides
    @Singleton
    fun provideFavoriteTrackModule(
        favoriteTrackDao: FavoriteTrackDao,
        db: FirebaseDatabase
    ): FavoriteTrackRepository = FavoriteTrackRepositoryImpl(favoriteTrackDao, db)

}