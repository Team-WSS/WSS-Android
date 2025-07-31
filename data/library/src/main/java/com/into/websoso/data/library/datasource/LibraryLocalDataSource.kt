package com.into.websoso.data.library.datasource

import androidx.paging.PagingSource
import com.into.websoso.data.library.model.NovelEntity

interface LibraryLocalDataSource {
    suspend fun insertNovels(novels: List<NovelEntity>)

    fun selectAllNovels(): PagingSource<Int, NovelEntity>

    suspend fun deleteAllNovels()
}
