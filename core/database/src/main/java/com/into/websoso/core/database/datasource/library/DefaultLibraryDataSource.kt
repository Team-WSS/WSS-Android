package com.into.websoso.core.database.datasource.library

import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

internal class DefaultLibraryDataSource
    @Inject
    constructor(
        private val novelDao: NovelDao,
    ) : LibraryLocalDataSource {
        override suspend fun selectAllNovels() {
            TODO("Not yet implemented")
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface LibraryDataSourceModule {
    @Binds
    @Singleton
    fun bindLibraryLocalDataSource(defaultLibraryDataSource: DefaultLibraryDataSource): LibraryLocalDataSource
}
