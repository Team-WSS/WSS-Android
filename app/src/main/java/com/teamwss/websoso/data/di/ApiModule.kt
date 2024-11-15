package com.teamwss.websoso.data.di

import com.teamwss.websoso.data.qualifier.Secured
import com.teamwss.websoso.data.qualifier.Unsecured
import com.teamwss.websoso.data.remote.api.*
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
    fun provideAuthApi(@Unsecured retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideNovelApi(@Secured retrofit: Retrofit): NovelApi = retrofit.create(NovelApi::class.java)

    @Provides
    @Singleton
    fun provideUserNovelApi(@Secured retrofit: Retrofit): UserNovelApi = retrofit.create(UserNovelApi::class.java)

    @Provides
    @Singleton
    fun provideNoticeApi(@Secured retrofit: Retrofit): NoticeApi = retrofit.create(NoticeApi::class.java)

    @Provides
    @Singleton
    fun provideFeedApi(@Secured retrofit: Retrofit): FeedApi = retrofit.create(FeedApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(@Secured retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideKeywordApi(@Secured retrofit: Retrofit): KeywordApi = retrofit.create(KeywordApi::class.java)

    @Provides
    @Singleton
    fun provideAvatarApi(@Secured retrofit: Retrofit): AvatarApi = retrofit.create(AvatarApi::class.java)
}
