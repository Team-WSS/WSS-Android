package com.into.websoso.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.into.websoso.data.remote.api.FeedApi
import com.into.websoso.data.remote.api.NovelApi
import com.into.websoso.data.remote.api.PushMessageApi
import com.into.websoso.data.remote.api.UserApi
import com.into.websoso.data.remote.api.VersionApi
import com.into.websoso.data.repository.AuthRepository
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.data.repository.NovelRepository
import com.into.websoso.data.repository.PushMessageRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.data.repository.VersionRepository
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

    @Provides
    @Singleton
    fun provideVersionRepository(versionApi: VersionApi): VersionRepository = VersionRepository(versionApi)

    @Provides
    @Singleton
    fun providePushMessageRepository(
        userRepository: UserRepository,
        authRepository: AuthRepository,
        userStorage: DataStore<Preferences>,
        pushMessageApi: PushMessageApi,
    ): PushMessageRepository = PushMessageRepository(userRepository, authRepository, userStorage, pushMessageApi)
}
