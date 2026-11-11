package com.into.websoso.data.collection

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CollectionApiModule {
    @Provides
    @Singleton
    fun provideCollectionApi(retrofit: Retrofit): CollectionApi = retrofit.create(CollectionApi::class.java)
}
