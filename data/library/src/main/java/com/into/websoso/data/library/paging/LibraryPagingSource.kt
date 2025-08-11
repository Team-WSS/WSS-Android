package com.into.websoso.data.library.paging

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult.Error
import androidx.paging.PagingSource.LoadResult.Page
import androidx.paging.PagingState
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.UserNovelsEntity

class LibraryPagingSource(
    private val getNovels: suspend (lastUserNovelId: Long) -> Result<UserNovelsEntity>,
) : PagingSource<Long, NovelEntity>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, NovelEntity> {
        val lastUserNovelId = params.key ?: DEFAULT_LAST_USER_NOVEL_ID

        return getNovels(lastUserNovelId).fold(
            onSuccess = { result ->
                val nextKey = if (result.isLoadable && result.userNovels.isNotEmpty()) {
                    result.userNovels.last().userNovelId
                } else {
                    null
                }

                Page(
                    data = result.userNovels,
                    prevKey = null,
                    nextKey = nextKey,
                )
            },
            onFailure = { throwable ->
                Error(throwable)
            },
        )
    }

    override fun getRefreshKey(state: PagingState<Long, NovelEntity>): Long = DEFAULT_LAST_USER_NOVEL_ID

    companion object {
        private const val DEFAULT_LAST_USER_NOVEL_ID = 0L
    }
}
