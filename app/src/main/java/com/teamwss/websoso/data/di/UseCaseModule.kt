package com.teamwss.websoso.data.di

import com.teamwss.websoso.data.repository.FakeFeedRepository
import com.teamwss.websoso.domain.usecase.GetFeedsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetFeedsUseCase(): GetFeedsUseCase = GetFeedsUseCase(FakeFeedRepository())
}