package com.teamwss.websoso.data.di

import com.teamwss.websoso.data.remote.api.NoticeApi
import com.teamwss.websoso.data.remote.api.NovelApi
import com.teamwss.websoso.data.remote.api.UserNovelApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideNovelApi(retrofit: Retrofit): NovelApi = retrofit.create()

    @Provides
    @Singleton
    fun provideUserNovelApi(retrofit: Retrofit): UserNovelApi = retrofit.create()

    @Provides
    @Singleton
    fun provideNoticeApi(retrofit: Retrofit): NoticeApi = retrofit.create()
}
