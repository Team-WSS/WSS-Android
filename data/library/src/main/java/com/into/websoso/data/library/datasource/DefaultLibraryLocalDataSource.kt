package com.into.websoso.data.library.datasource

import com.into.websoso.data.library.model.NovelEntity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultLibraryLocalDataSource
    @Inject
    constructor() : LibraryLocalDataSource {
        private val cachedNovels = MutableStateFlow<List<NovelEntity>>(emptyList())

        override suspend fun insertNovels(novels: List<NovelEntity>) {
            cachedNovels.update { currentNovels ->
                (currentNovels + novels).distinctBy { it.novelId }
            }
        }

        override suspend fun insertNovel(novel: NovelEntity) {
            cachedNovels.update { currentNovels ->
                if (currentNovels.any { it.novelId == novel.novelId }) return
                (currentNovels + novel).distinctBy { it.novelId }
            }
        }

        override fun selectAllNovels(): Flow<List<NovelEntity>> = cachedNovels.asStateFlow()

        override suspend fun selectNovel(novelId: Long): NovelEntity? =
            cachedNovels.value.find { currentNovel ->
                currentNovel.novelId == novelId
            }

        override suspend fun updateNovels(novels: List<NovelEntity>) {
            cachedNovels.update { currentNovels ->
                val updatedNovels = novels.associateBy { it.novelId }
                currentNovels.map { currentNovel ->
                    updatedNovels[currentNovel.novelId] ?: currentNovel
                }
            }
        }

        override suspend fun updateNovel(novel: NovelEntity) {
            cachedNovels.update { currentNovels ->
                currentNovels.map { currentNovel ->
                    if (currentNovel.novelId == novel.novelId) novel else currentNovel
                }
            }
        }

        override suspend fun deleteAllNovels() {
            cachedNovels.value = emptyList()
        }

        override suspend fun deleteNovel(novelId: Long) {
            cachedNovels.update { currentNovels ->
                currentNovels.filterNot { currentNovel -> currentNovel.novelId == novelId }
            }
        }
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface LibraryDataSourceModule {
    @Binds
    @Singleton
    fun bindLibraryLocalDataSource(defaultLibraryLocalDataSource: DefaultLibraryLocalDataSource): LibraryLocalDataSource
}
