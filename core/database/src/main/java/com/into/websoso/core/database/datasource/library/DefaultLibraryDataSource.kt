package com.into.websoso.core.database.datasource.library

import com.into.websoso.core.database.entity.InDatabaseNovelEntity
import com.into.websoso.core.database.entity.toInDatabaseEntity
import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.model.NovelEntity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

internal class DefaultLibraryDataSource
    @Inject
    constructor(
        private val novelDao: NovelDao,
    ) : LibraryLocalDataSource {
        override suspend fun insertNovels(novels: List<NovelEntity>) {
            novelDao.insertNovels(novels.map(NovelEntity::toInDatabaseEntity))
        }

        override suspend fun insertNovel(novel: NovelEntity) {
            novelDao.insertNovel(novel.toInDatabaseEntity())
        }

        override fun selectAllNovels(): Flow<List<NovelEntity>> =
            novelDao.selectAllNovels().map { novelsInDatabase ->
                novelsInDatabase.map(InDatabaseNovelEntity::toData)
            }

        override suspend fun selectNovel(novelId: Long): NovelEntity = novelDao.selectNovelById(novelId).toData()

        override suspend fun updateNovels(novels: List<NovelEntity>) {
            novelDao.updateNovels(novels.map(NovelEntity::toInDatabaseEntity))
        }

        override suspend fun updateNovel(novel: NovelEntity) {
            novelDao.updateNovel(novel.toInDatabaseEntity())
        }

        override suspend fun deleteAllNovels() {
            novelDao.deleteAllNovels()
        }

        override suspend fun deleteNovel(novelId: Long) {
            novelDao.deleteNovelById(novelId)
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface LibraryDataSourceModule {
    @Binds
    @Singleton
    fun bindLibraryLocalDataSource(defaultLibraryDataSource: DefaultLibraryDataSource): LibraryLocalDataSource
}
