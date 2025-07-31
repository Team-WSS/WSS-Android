package com.into.websoso.data.library.datasource

import androidx.paging.PagingSource
import com.into.websoso.core.database.dao.NovelDao
import com.into.websoso.core.database.entity.InDatabaseNovelEntity
import com.into.websoso.data.library.model.NovelEntity
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

internal class DefaultLibraryLocalDataSource
    @Inject
    constructor(
        private val novelDao: NovelDao,
    ) : LibraryLocalDataSource {
        override suspend fun insertNovels(novels: List<NovelEntity>) {
            val offset = novelDao.selectNovelsCount()
            novelDao.insertNovels(
                novels.mapIndexed { index, novelEntity ->
                    novelEntity.toNovelDatabase(offset + index)
                },
            )
        }

        override fun selectAllNovels(): PagingSource<Int, InDatabaseNovelEntity> = novelDao.selectAllNovels()

        override suspend fun deleteAllNovels() {
            novelDao.deleteAllNovels()
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface LibraryDataSourceModule {
    @Binds
    @Singleton
    fun bindLibraryDataSource(defaultLibraryLocalDataSource: DefaultLibraryLocalDataSource): LibraryLocalDataSource
}
