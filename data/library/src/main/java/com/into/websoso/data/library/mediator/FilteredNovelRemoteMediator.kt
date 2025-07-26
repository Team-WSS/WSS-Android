package com.into.websoso.data.library.mediator

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
import com.into.websoso.data.library.datasource.FilteredLibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource

@OptIn(ExperimentalPagingApi::class)
class FilteredNovelRemoteMediator(
    private val userId: Long,
    private val libraryRemoteDataSource: LibraryRemoteDataSource,
    private val filteredLibraryLocalDataSource: FilteredLibraryLocalDataSource,
    private val isInterested: Boolean,
    private val sortCriteria: String,
    private val readStatuses: List<String>,
    private val attractivePoints: List<String>,
    private val novelRating: Float,
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
                sortCriteria = sortCriteria,
                isInterest = if (!isInterested) null else true,
                readStatuses = readStatuses.ifEmpty { null },
                attractivePoints = attractivePoints.ifEmpty { null },
                novelRating = if (novelRating.isCloseTo(DEFAULT_NOVEL_RATING)) null else novelRating,
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
