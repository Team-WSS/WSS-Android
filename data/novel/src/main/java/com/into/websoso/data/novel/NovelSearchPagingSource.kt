package com.into.websoso.data.novel

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.into.websoso.data.novel.model.NovelSearchEntity

internal class NovelSearchPagingSource(
    private val query: String,
    private val api: NovelSearchApi,
) : PagingSource<Int, NovelSearchEntity>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NovelSearchEntity> {
        val page = params.key ?: INITIAL_PAGE
        val response = api.getNovels(
                query = query,
                page = page,
                size = params.loadSize,
            )

        return LoadResult.Page(
            data = response.novels.map(NovelSearchResponseDto.NovelDto::toData),
            prevKey = page.takeIf { it > INITIAL_PAGE }?.minus(1),
            nextKey = (page + 1).takeIf { response.isLoadable },
        )
    }

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
