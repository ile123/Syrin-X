package com.ile.syrin_x.di

import com.ile.syrin_x.data.api.SoundCloudAuthApi
import com.ile.syrin_x.data.database.SoundCloudDao
import com.ile.syrin_x.data.repository.musicsource.SoundCloudAuthRepositoryImpl
import com.ile.syrin_x.domain.repository.SoundCloudAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SoundCloudModule {

    @Provides
    @Singleton
    fun provideSoundCloudAuthRepository(
        api: SoundCloudAuthApi,
        dao: SoundCloudDao
    ): SoundCloudAuthRepository = SoundCloudAuthRepositoryImpl(api, dao)
}
