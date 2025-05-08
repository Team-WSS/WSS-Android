package com.into.websoso.data.di

import com.into.websoso.data.remote.api.AuthApi
import com.into.websoso.data.remote.api.AvatarApi
import com.into.websoso.data.remote.api.FeedApi
import com.into.websoso.data.remote.api.KeywordApi
import com.into.websoso.data.remote.api.NotificationApi
import com.into.websoso.data.remote.api.NovelApi
import com.into.websoso.data.remote.api.PushMessageApi
import com.into.websoso.data.remote.api.UserApi
import com.into.websoso.data.remote.api.UserNovelApi
import com.into.websoso.data.remote.api.VersionApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideNovelApi(retrofit: Retrofit): NovelApi = retrofit.create(NovelApi::class.java)

    @Provides
    @Singleton
    fun provideUserNovelApi(retrofit: Retrofit): UserNovelApi = retrofit.create(UserNovelApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi = retrofit.create(NotificationApi::class.java)

    @Provides
    @Singleton
    fun provideFeedApi(retrofit: Retrofit): FeedApi = retrofit.create(FeedApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideKeywordApi(retrofit: Retrofit): KeywordApi = retrofit.create(KeywordApi::class.java)

    @Provides
    @Singleton
    fun provideAvatarApi(retrofit: Retrofit): AvatarApi = retrofit.create(AvatarApi::class.java)

    @Provides
    @Singleton
    fun provideVersionApi(retrofit: Retrofit): VersionApi = retrofit.create(VersionApi::class.java)

    @Provides
    @Singleton
    fun providePushMessageApi(retrofit: Retrofit): PushMessageApi = retrofit.create(PushMessageApi::class.java)
}
