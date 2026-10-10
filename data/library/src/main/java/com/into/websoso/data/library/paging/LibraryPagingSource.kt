package com.into.websoso.data.library.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.UserNovelsEntity

class LibraryPagingSource(
    private val getNovels: suspend (cursor: String?) -> Result<UserNovelsEntity>,
) : PagingSource<String, NovelEntity>() {
    override suspend fun load(params: LoadParams<String>): LoadResult<String, NovelEntity> =
        getNovels(params.key).fold(
            onSuccess = { result ->
                val nextKey = if (result.isLoadable && result.userNovels.isNotEmpty()) {
                    result.nextCursor
                } else {
                    null
                }

                LoadResult.Page(
                    data = result.userNovels,
                    prevKey = null,
                    nextKey = nextKey,
                )
            },
            onFailure = { throwable ->
                LoadResult.Error(throwable)
            },
        )

    override fun getRefreshKey(state: PagingState<String, NovelEntity>): String? = null
}
