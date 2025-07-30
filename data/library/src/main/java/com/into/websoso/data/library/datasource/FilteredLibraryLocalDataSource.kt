package com.into.websoso.data.library.datasource

import androidx.paging.PagingSource
import com.into.websoso.core.database.dao.FilteredNovelDao
import com.into.websoso.core.database.entity.InDatabaseFilteredNovelEntity
import com.into.websoso.data.library.model.NovelEntity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

interface FilteredLibraryLocalDataSource {
    suspend fun insertNovels(novels: List<NovelEntity>)

    fun selectAllNovels(): PagingSource<Int, InDatabaseFilteredNovelEntity>

    suspend fun deleteAllNovels()
}

internal class DefaultFilteredLibraryLocalDataSource
    @Inject
    constructor(
        private val filteredNovelDao: FilteredNovelDao,
    ) : FilteredLibraryLocalDataSource {
        override suspend fun insertNovels(novels: List<NovelEntity>) {
            val offset = filteredNovelDao.selectNovelsCount()
            filteredNovelDao.insertFilteredNovels(
                novels.mapIndexed { index, novelEntity ->
                    novelEntity.toFilteredNovelDatabase(offset + index)
                },
            )
        }

        override fun selectAllNovels(): PagingSource<Int, InDatabaseFilteredNovelEntity> = filteredNovelDao.selectAllNovels()

        override suspend fun deleteAllNovels() {
            filteredNovelDao.clearAll()
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface FilteredLibraryDataSourceModule {
    @Binds
    @Singleton
    fun bindFilteredLibraryDataSource(
        defaultFilteredLibraryLocalDataSource: DefaultFilteredLibraryLocalDataSource,
    ): FilteredLibraryLocalDataSource
}
