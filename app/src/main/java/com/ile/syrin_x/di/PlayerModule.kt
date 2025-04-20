package com.ile.syrin_x.di

import android.content.Context
import com.ile.syrin_x.domain.player.UnifiedAudioPlayer
import com.ile.syrin_x.domain.repository.SpotifyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton
    fun provideUnifiedAudioPlayer(
        @ApplicationContext context: Context,
        spotifyRepository: SpotifyRepository
    ): UnifiedAudioPlayer {
        return UnifiedAudioPlayer(context, spotifyRepository)
    }
}
