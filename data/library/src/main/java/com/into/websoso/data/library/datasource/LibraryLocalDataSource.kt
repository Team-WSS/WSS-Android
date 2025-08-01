package com.into.websoso.data.library.datasource

import androidx.paging.PagingSource
import com.into.websoso.data.library.model.NovelEntity

interface LibraryLocalDataSource {
    suspend fun insertNovels(novels: List<NovelEntity>)

    suspend fun insertNovel(novel: NovelEntity)

    fun selectAllNovels(): PagingSource<Int, NovelEntity>

    suspend fun selectNovelByUserNovelId(userNovelId: Long): NovelEntity?

    suspend fun selectNovelByNovelId(novelId: Long): NovelEntity?

    suspend fun selectAllNovelsCount(): Int

    suspend fun deleteAllNovels()

    suspend fun deleteNovel(novelId: Long)
}
