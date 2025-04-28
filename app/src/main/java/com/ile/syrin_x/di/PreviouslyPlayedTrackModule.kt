package com.ile.syrin_x.di

import com.google.firebase.database.FirebaseDatabase
import com.ile.syrin_x.data.database.FavoriteArtistDao
import com.ile.syrin_x.data.database.PreviouslyPlayedTrackDao
import com.ile.syrin_x.data.repository.history.PreviouslyPlayedTrackRepositoryImpl
import com.ile.syrin_x.data.repository.usercreatedplaylist.FavoriteArtistRepositoryImpl
import com.ile.syrin_x.domain.repository.FavoriteArtistRepository
import com.ile.syrin_x.domain.repository.PreviouslyPlayedTrackRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreviouslyPlayedTrackModule{

    @Provides
    @Singleton
    fun providePreviouslyPlayedTrackModule(
        previouslyPlayedTrackDao: PreviouslyPlayedTrackDao,
        db: FirebaseDatabase
    ): PreviouslyPlayedTrackRepository = PreviouslyPlayedTrackRepositoryImpl(previouslyPlayedTrackDao, db)

}