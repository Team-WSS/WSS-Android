package com.into.websoso.data.library.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.UserNovelsEntity

class LibraryPagingSource(
    private val getNovels: suspend (lastUserNovelId: Long) -> Result<UserNovelsEntity>,
) : PagingSource<Long, NovelEntity>() {

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, NovelEntity> {
        val currentKey = params.key ?: DEFAULT_LAST_USER_NOVEL_ID

        return getNovels(currentKey).fold(
            onSuccess = { result ->
                val novels = result.userNovels

                val nextKey = if (result.isLoadable && novels.isNotEmpty()) {
                    novels.last().userNovelId
                } else {
                    null
                }

                LoadResult.Page(
                    data = novels,
                    prevKey = null,
                    nextKey = nextKey,
                )
            },
            onFailure = { throwable ->
                LoadResult.Error(throwable)
            },
        )
    }

    override fun getRefreshKey(state: PagingState<Long, NovelEntity>): Long? {
        return null
    }

    companion object {
        private const val DEFAULT_LAST_USER_NOVEL_ID = 0L
    }
}
