package com.into.websoso.data.novel

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NovelSearchApiModule {
    @Provides
    @Singleton
    fun provideNovelSearchApi(retrofit: Retrofit): NovelSearchApi = retrofit.create(NovelSearchApi::class.java)
}
