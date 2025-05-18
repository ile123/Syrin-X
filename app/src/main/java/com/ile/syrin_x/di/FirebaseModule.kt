package com.ile.syrin_x.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.ile.syrin_x.data.database.FavoriteArtistDao
import com.ile.syrin_x.data.database.NewReleaseNotificationDao
import com.ile.syrin_x.data.repository.firebase.AuthenticationRepositoryImpl
import com.ile.syrin_x.data.repository.firebase.UserRepositoryImpl
import com.ile.syrin_x.domain.repository.AuthenticationRepository
import com.ile.syrin_x.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseRealtimeDatabase(): FirebaseDatabase = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideAuthenticationRepository(
        auth: FirebaseAuth
    ): AuthenticationRepository = AuthenticationRepositoryImpl(auth)

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideUserRepository(
        db: FirebaseDatabase,
        storage: FirebaseStorage,
        newReleaseNotificationDao: NewReleaseNotificationDao
    ): UserRepository = UserRepositoryImpl(db, storage, newReleaseNotificationDao)
}