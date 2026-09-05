package com.into.websoso.data.novel

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.into.websoso.data.novel.model.NovelSearchEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NovelSearchRepository
    @Inject
    internal constructor(
        private val api: NovelSearchApi,
    ) {
        fun searchNovels(query: String): Flow<PagingData<NovelSearchEntity>> =
            Pager(
                config = PagingConfig(
                    pageSize = PAGE_SIZE,
                    initialLoadSize = PAGE_SIZE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    NovelSearchPagingSource(
                        query = query,
                        api = api,
                    )
                },
            ).flow

        private companion object {
            const val PAGE_SIZE = 20
        }
    }
