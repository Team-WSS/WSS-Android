package com.teamwss.websoso.data.di

import com.teamwss.websoso.data.remote.api.FeedApi
import com.teamwss.websoso.data.remote.api.NovelApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
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
    fun provideNovelService(retrofit: Retrofit) = NetworkModule.provideApi<NovelApi>(
        NetworkModule.provideRetrofit(
            NetworkModule.provideLogOkHttpClient()
        )
    )
}
