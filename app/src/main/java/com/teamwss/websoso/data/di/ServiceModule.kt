package com.teamwss.websoso.data.di

import com.teamwss.websoso.data.remote.api.FeedApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun providesFeedApi(retrofit: Retrofit): FeedApi = retrofit.create()
}
