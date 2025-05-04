package com.ile.syrin_x.di

import com.ile.syrin_x.data.api.DeezerApi
import com.ile.syrin_x.data.repository.musicsource.DeezerRepositoryImpl
import com.ile.syrin_x.domain.repository.DeezerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DeezerModule {

    @Provides
    @Singleton
    fun provideDeezerRepository(
        api: DeezerApi,
    ): DeezerRepository = DeezerRepositoryImpl(api)
}