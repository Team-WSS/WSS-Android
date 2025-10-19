package com.into.websoso.data.library

import androidx.paging.PagingData
import com.into.websoso.data.library.model.NovelEntity
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    val libraryFlow: Flow<PagingData<NovelEntity>>

    suspend fun refresh()

    companion object {
        const val PAGE_SIZE = 20
    }
}
