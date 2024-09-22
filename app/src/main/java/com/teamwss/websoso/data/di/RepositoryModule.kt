package com.teamwss.websoso.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.teamwss.websoso.data.remote.api.FeedApi
import com.teamwss.websoso.data.remote.api.NovelApi
import com.teamwss.websoso.data.remote.api.UserApi
import com.teamwss.websoso.data.repository.FeedRepository
import com.teamwss.websoso.data.repository.NovelRepository
import com.teamwss.websoso.data.repository.UserRepository
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

    @Provides
    @Singleton
    fun provideUserRepository(
        userApi: UserApi,
        userStorage: DataStore<Preferences>,
    ): UserRepository = UserRepository(userApi, userStorage)

    @Provides
    @Singleton
    fun provideNovelRepository(novelApi: NovelApi): NovelRepository = NovelRepository(novelApi)
}
