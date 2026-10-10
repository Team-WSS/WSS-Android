package com.into.websoso.data.collection

import com.into.websoso.domain.collection.CollectionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CollectionRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCollectionRepository(repository: DefaultCollectionRepository): CollectionRepository
}
