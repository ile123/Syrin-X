package com.ile.syrin_x.di

import com.google.firebase.database.FirebaseDatabase
import com.ile.syrin_x.data.database.FavoriteArtistDao
import com.ile.syrin_x.data.repository.usercreatedplaylist.FavoriteArtistRepositoryImpl
import com.ile.syrin_x.domain.repository.FavoriteArtistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FavoriteArtistModule{

    @Provides
    @Singleton
    fun provideFavoriteArtistModule(
        favoriteArtistDao: FavoriteArtistDao,
        db: FirebaseDatabase
    ): FavoriteArtistRepository = FavoriteArtistRepositoryImpl(favoriteArtistDao, db)

}