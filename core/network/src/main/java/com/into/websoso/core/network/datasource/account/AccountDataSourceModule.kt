package com.into.websoso.core.network.datasource.account

import com.into.websoso.data.account.AccountRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface AccountDataSourceModule {
    @Binds
    @Singleton
    fun bindAccountRemoteDataSource(defaultAccountDataSource: DefaultAccountDataSource): AccountRemoteDataSource
}
