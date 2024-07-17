package com.teamwss.websoso.data.di

import com.teamwss.websoso.data.repository.FeedRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providesFeedRepository(): FeedRepository = FeedRepository()
}
