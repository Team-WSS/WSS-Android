package com.into.websoso.core.database.datasource.library

import androidx.paging.PagingSource
import androidx.room.Transaction
import com.into.websoso.core.database.datasource.library.dao.NovelDao
import com.into.websoso.core.database.datasource.library.entity.InDatabaseNovelEntity
import com.into.websoso.core.database.datasource.library.mapper.toData
import com.into.websoso.core.database.datasource.library.mapper.toNovelDatabase
import com.into.websoso.core.database.datasource.library.paging.mapValue
import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.model.NovelEntity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

internal class DefaultLibraryLocalDataSource
    @Inject
    constructor(
        private val novelDao: NovelDao,
    ) : LibraryLocalDataSource {
        @Transaction
        override suspend fun insertNovels(novels: List<NovelEntity>) {
            novelDao.apply {
                val offset = selectNovelsCount()

                insertNovels(
                    novels.mapIndexed { index, novelEntity ->
                        novelEntity.toNovelDatabase(offset + index)
                    },
                )
            }
        }

        @Transaction
        override suspend fun insertNovel(novel: NovelEntity) {
            novelDao.apply {
                val novelIndex = selectNovelByUserNovelId(novel.userNovelId)?.sortIndex ?: 0

                insertNovel(novel.toNovelDatabase(novelIndex))
            }
        }

        override fun selectAllNovels(): PagingSource<Int, NovelEntity> = novelDao.selectAllNovels().mapValue(InDatabaseNovelEntity::toData)

        override suspend fun selectNovelByUserNovelId(userNovelId: Long): NovelEntity? =
            novelDao.selectNovelByUserNovelId(userNovelId)?.toData()

        override suspend fun selectNovelByNovelId(novelId: Long): NovelEntity? = novelDao.selectNovelByNovelId(novelId)?.toData()

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
