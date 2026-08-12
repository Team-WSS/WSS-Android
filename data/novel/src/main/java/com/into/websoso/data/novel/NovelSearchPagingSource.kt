package com.into.websoso.data.novel

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.into.websoso.data.novel.model.NovelSearchEntity

internal class NovelSearchPagingSource(
    private val query: String,
    private val api: NovelSearchApi,
) : PagingSource<Int, NovelSearchEntity>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NovelSearchEntity> =
        LoadResult.Page(
            data = api.getNovels(
                query = query,
                page = params.key ?: INITIAL_PAGE,
                size = params.loadSize,
            ).novels.map(NovelSearchResponseDto.NovelDto::toData),
            prevKey = null,
            nextKey = null,
        )

    override fun getRefreshKey(state: PagingState<Int, NovelSearchEntity>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.let { page ->
                page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
            }
        }

    private companion object {
        const val INITIAL_PAGE = 0
    }
}
