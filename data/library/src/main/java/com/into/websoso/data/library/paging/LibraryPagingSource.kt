package com.into.websoso.data.library.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.into.websoso.core.common.extensions.isCloseTo
import com.into.websoso.data.filter.model.LibraryFilter
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.NovelEntity

class LibraryPagingSource(
    private val userId: Long,
    private val libraryRemoteDataSource: LibraryRemoteDataSource,
    private val filterParams: LibraryFilter,
) : PagingSource<Long, NovelEntity>() {
    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, NovelEntity> {
        val lastUserNovelId = params.key ?: DEFAULT_LAST_USER_NOVEL_ID

        return try {
            val response = libraryRemoteDataSource.getUserNovels(
                userId = userId,
                lastUserNovelId = lastUserNovelId,
                size = params.loadSize,
                sortCriteria = filterParams.sortCriteria,
                isInterest = if (!filterParams.isInterested) null else true,
                readStatuses = filterParams.readStatusKeys.ifEmpty { null },
                attractivePoints = filterParams.attractivePointKeys.ifEmpty { null },
                novelRating = if (filterParams.novelRating.isCloseTo(0f)) null else filterParams.novelRating,
                query = null,
                updatedSince = null,
            )

            val nextKey = if (response.isLoadable && response.userNovels.isNotEmpty()) {
                response.userNovels.last().userNovelId
            } else {
                null
            }

            LoadResult.Page(
                data = response.userNovels,
                prevKey = null,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Long, NovelEntity>): Long? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.userNovelId
        }

    companion object {
        private const val DEFAULT_LAST_USER_NOVEL_ID = 0L
    }
}
