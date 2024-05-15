package com.teamwss.websoso.data.di

import com.teamwss.websoso.data.repository.FakeFeedRepository
import com.teamwss.websoso.data.repository.FakeSosoPickRepository
import com.teamwss.websoso.data.repository.FakeUserRepository
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
    fun provideFakeFeedRepository() = FakeFeedRepository()

    @Provides
    @Singleton
    fun provideFakeSoSoPickRepository() = FakeSosoPickRepository()

    @Provides
    @Singleton
    fun provideFakeUserRepository() = FakeUserRepository()
}