package com.teamwss.websoso.data.di

import com.teamwss.websoso.data.remote.api.FeedApi
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
    fun provideFeedRepository(feedApi: FeedApi): FeedRepository = FeedRepository(feedApi)
}
