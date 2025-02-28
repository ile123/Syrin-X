package com.ile.syrin_x.di

import com.google.firebase.auth.FirebaseAuth
import com.ile.syrin_x.data.repository.AuthenticationRepositoryImpl
import com.ile.syrin_x.domain.repository.AuthenticationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthenticationRepository(
        auth: FirebaseAuth
    ): AuthenticationRepository = AuthenticationRepositoryImpl(auth)

}