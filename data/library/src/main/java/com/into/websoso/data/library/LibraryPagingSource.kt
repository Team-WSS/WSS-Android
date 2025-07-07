package com.into.websoso.data.library

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.NovelEntity

private const val STARTING_USER_NOVEL_ID = 0

internal class LibraryPagingSource(
    private val libraryRemoteDataSource: LibraryRemoteDataSource,
    private val userId: Long,
    private val lastUserNovelId: Long,
    private val size: Int,
    private val sortCriteria: String,
    private val isInterest: Boolean?,
    private val readStatuses: List<String>?,
    private val attractivePoints: List<String>?,
    private val novelRating: Float?,
    private val query: String?,
    private val updatedSince: String?,
) : PagingSource<Int, NovelEntity>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NovelEntity> {
        val userNovelId = params.key ?: STARTING_USER_NOVEL_ID

        return try {
            val response = libraryRemoteDataSource.getUserNovels(
                userId = userId,
                lastUserNovelId = lastUserNovelId,
                size = size,
                sortCriteria = sortCriteria,
                isInterest = isInterest,
                readStatuses = readStatuses,
                attractivePoints = attractivePoints,
                novelRating = novelRating,
                query = query,
                updatedSince = updatedSince,
            )
            val nextKey = if (response.isLoadable.not()) {
                null
            } else {
                response.userNovels
                    .minOf {
                        it.userNovelId
                    }.toInt()
            }

            LoadResult.Page(
                data = response.userNovels,
                prevKey = if (userNovelId == STARTING_USER_NOVEL_ID) null else userNovelId - 1,
                nextKey = nextKey,
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, NovelEntity>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
}
