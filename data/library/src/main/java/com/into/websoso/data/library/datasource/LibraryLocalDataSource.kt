package com.into.websoso.data.library.datasource

import androidx.paging.PagingSource
import com.into.websoso.core.database.dao.NovelDao
import com.into.websoso.core.database.entity.InDatabaseNovelEntity
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.toDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

interface LibraryLocalDataSource {
    suspend fun insertNovels(novels: List<NovelEntity>)

    fun selectAllNovels(): PagingSource<Int, InDatabaseNovelEntity>

    suspend fun deleteAllNovels()
}

internal class DefaultLibraryDataSource
    @Inject
    constructor(
        private val novelDao: NovelDao,
    ) : LibraryLocalDataSource {
        override suspend fun insertNovels(novels: List<NovelEntity>) {
            novelDao.insertNovels(novels.map(NovelEntity::toDatabase))
        }

        override fun selectAllNovels(): PagingSource<Int, InDatabaseNovelEntity> = novelDao.selectAllNovels()

        override suspend fun deleteAllNovels() {
            novelDao.clearAll()
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface LibraryDataSourceModule {
    @Binds
    @Singleton
    fun bindLibraryLocalDataSource(defaultLibraryDataSource: DefaultLibraryDataSource): LibraryLocalDataSource
}
