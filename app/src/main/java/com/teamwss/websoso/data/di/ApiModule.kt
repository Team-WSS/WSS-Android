package com.teamwss.websoso.data.di

import com.teamwss.websoso.data.remote.api.FeedApi
import com.teamwss.websoso.data.remote.api.NovelApi
import com.teamwss.websoso.data.remote.api.UserNovelApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    val feedApi = NetworkModule.provideApi<FeedApi>(
        NetworkModule.provideRetrofit(
            NetworkModule.provideLogOkHttpClient()
        )
    )

    @Provides
    @Singleton
    fun provideNovelApi() = NetworkModule.provideApi<NovelApi>(
        NetworkModule.provideRetrofit(
            NetworkModule.provideLogOkHttpClient()
        )
    )

    @Provides
    @Singleton
    fun provideUserNovelApi() = NetworkModule.provideApi<UserNovelApi>(
        NetworkModule.provideRetrofit(
            NetworkModule.provideLogOkHttpClient()
        )
    )
}
