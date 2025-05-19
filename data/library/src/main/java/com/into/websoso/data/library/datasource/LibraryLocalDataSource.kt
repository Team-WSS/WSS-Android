package com.into.websoso.data.library.datasource

import com.into.websoso.data.library.model.NovelEntity
import kotlinx.coroutines.flow.Flow

interface LibraryLocalDataSource {
    suspend fun insertNovels(novels: List<NovelEntity>)

    suspend fun insertNovel(novel: NovelEntity)

    fun selectAllNovels(): Flow<List<NovelEntity>>

    suspend fun selectNovel(novelId: Long): NovelEntity

    suspend fun updateNovels(novels: List<NovelEntity>)

    suspend fun updateNovel(novel: NovelEntity)

    suspend fun deleteAllNovels()

    suspend fun deleteNovel(novelId: Long)
}
