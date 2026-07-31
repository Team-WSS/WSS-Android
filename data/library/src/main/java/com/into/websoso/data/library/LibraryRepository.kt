package com.into.websoso.data.library

import androidx.paging.PagingData
import com.into.websoso.data.library.model.LibraryKeywordEntity
import com.into.websoso.data.library.model.NovelEntity
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    val novelTotalCount: Flow<Long>

    fun getLibraryFlow(): Flow<PagingData<NovelEntity>>

    fun getUnfilteredLibraryFlow(): Flow<PagingData<NovelEntity>>

    suspend fun getRegisteredKeywords(): List<LibraryKeywordEntity>

    companion object {
        const val PAGE_SIZE = 20
    }
}
