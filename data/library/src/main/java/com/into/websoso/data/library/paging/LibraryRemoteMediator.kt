package com.into.websoso.data.library.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.APPEND
import androidx.paging.LoadType.PREPEND
import androidx.paging.LoadType.REFRESH
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.MediatorResult.Error
import androidx.paging.RemoteMediator.MediatorResult.Success
import com.into.websoso.core.common.extensions.isCloseTo
import com.into.websoso.core.database.entity.InDatabaseFilteredNovelEntity
import com.into.websoso.data.filter.model.LibraryFilter
import com.into.websoso.data.library.datasource.FilteredLibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource

@OptIn(ExperimentalPagingApi::class)
class LibraryRemoteMediator(
    private val userId: Long,
    private val libraryRemoteDataSource: LibraryRemoteDataSource,
    private val filteredLibraryLocalDataSource: FilteredLibraryLocalDataSource,
    private val libraryFilter: LibraryFilter,
) : RemoteMediator<Int, InDatabaseFilteredNovelEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, InDatabaseFilteredNovelEntity>,
    ): MediatorResult {
        val lastUserNovelId = when (loadType) {
            REFRESH -> DEFAULT_LAST_USER_NOVEL_ID
            PREPEND -> return Success(endOfPaginationReached = true)
            APPEND -> state.lastItemOrNull()?.userNovelId ?: return Success(true)
        }

        return try {
            val response = libraryRemoteDataSource.getUserNovels(
                userId = userId,
                lastUserNovelId = lastUserNovelId,
                size = state.config.pageSize,
                sortCriteria = libraryFilter.sortCriteria,
                isInterest = if (!libraryFilter.isInterested) null else true,
                readStatuses = libraryFilter.readStatusKeys.ifEmpty { null },
                attractivePoints = libraryFilter.attractivePointKeys.ifEmpty { null },
                novelRating = if (libraryFilter.novelRating.isCloseTo(DEFAULT_NOVEL_RATING)) {
                    null
                } else {
                    libraryFilter.novelRating
                },
                query = null,
                updatedSince = null,
            )

            if (loadType == REFRESH) filteredLibraryLocalDataSource.deleteAllNovels()

            filteredLibraryLocalDataSource.insertNovels(response.userNovels)

            Success(endOfPaginationReached = !response.isLoadable)
        } catch (e: Exception) {
            Error(e)
        }
    }

    companion object {
        private const val DEFAULT_LAST_USER_NOVEL_ID = 0L
        private const val DEFAULT_NOVEL_RATING = 0f
    }
}
