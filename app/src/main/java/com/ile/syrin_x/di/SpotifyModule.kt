package com.ile.syrin_x.di

import com.ile.syrin_x.data.api.SpotifyApi
import com.ile.syrin_x.data.api.SpotifyAuthApi
import com.ile.syrin_x.data.database.SpotifyDao
import com.ile.syrin_x.data.repository.musicsource.SpotifyAuthRepositoryImpl
import com.ile.syrin_x.data.repository.musicsource.SpotifyRepositoryImpl
import com.ile.syrin_x.domain.repository.SpotifyAuthRepository
import com.ile.syrin_x.domain.repository.SpotifyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpotifyModule {

    @Provides
    @Singleton
    fun provideSpotifyAuthRepository(
        @Named("AccountApi") api: SpotifyAuthApi,
        dao: SpotifyDao
    ): SpotifyAuthRepository = SpotifyAuthRepositoryImpl(api, dao)

    @Provides
    @Singleton
    fun provideSpotifyRepository(
        @Named("MainApi") api: SpotifyApi
    ): SpotifyRepository = SpotifyRepositoryImpl(api)
}

