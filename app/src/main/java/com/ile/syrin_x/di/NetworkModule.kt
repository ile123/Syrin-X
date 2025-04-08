package com.ile.syrin_x.di

import com.ile.syrin_x.data.api.SoundCloudApi
import com.ile.syrin_x.data.api.SoundCloudAuthApi
import com.ile.syrin_x.data.api.SpotifyApi
import com.ile.syrin_x.data.api.SpotifyAuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val SPOTIFY_ACCOUNT_URL = "https://accounts.spotify.com/"
    private const val SPOTIFY_BASE_URL = "https://api.spotify.com/"
    private const val SOUNDCLOUD_BASE_URL = "https://api.soundcloud.com/"

    // I have to provide a placeholder BASE URL, because this will be overwritten later
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://djecaci.net/biografija/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("AccountApi")
    fun provideSpotifyAccountApi(retrofit: Retrofit): SpotifyAuthApi {
        return retrofit.newBuilder()
            .baseUrl(SPOTIFY_ACCOUNT_URL)
            .build()
            .create(SpotifyAuthApi::class.java)
    }

    @Provides
    @Singleton
    @Named("MainApi")
    fun provideSpotifyApi(retrofit: Retrofit): SpotifyApi {
        return retrofit.newBuilder()
            .baseUrl(SPOTIFY_BASE_URL)
            .build()
            .create(SpotifyApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSoundCloudAuthApi(retrofit: Retrofit): SoundCloudAuthApi {
        return retrofit.newBuilder()
            .baseUrl(SOUNDCLOUD_BASE_URL)
            .build()
            .create(SoundCloudAuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSoundCloudApi(retrofit: Retrofit): SoundCloudApi {
        return retrofit.newBuilder()
            .baseUrl(SOUNDCLOUD_BASE_URL)
            .build()
            .create(SoundCloudApi::class.java)
    }
}
