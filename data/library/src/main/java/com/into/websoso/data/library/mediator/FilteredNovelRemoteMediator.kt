package com.into.websoso.data.library.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
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
    private val novelRating: Float?,
) : RemoteMediator<Int, InDatabaseFilteredNovelEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, InDatabaseFilteredNovelEntity>,
    ): MediatorResult {
        val lastUserNovelId = when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                when (sortCriteria) {
                    "RECENT" -> state.lastItemOrNull()?.userNovelId
                    "OLD" -> state.firstItemOrNull()?.userNovelId
                    else -> null
                } ?: return MediatorResult.Success(true)
            }
        } ?: DEFAULT_LAST_USER_NOVEL_ID

        return try {
            val response = libraryRemoteDataSource.getUserNovels(
                userId = userId,
                lastUserNovelId = lastUserNovelId,
                size = state.config.pageSize,
                sortCriteria = sortCriteria,
                isInterest = if (!isInterested) null else true,
                readStatuses = readStatuses.ifEmpty { null },
                attractivePoints = attractivePoints.ifEmpty { null },
                novelRating = if (novelRating == 0f) null else novelRating,
                query = null,
                updatedSince = null,
            )

            if (loadType == LoadType.REFRESH) {
                filteredLibraryLocalDataSource.deleteAllNovels()
            }
            filteredLibraryLocalDataSource.insertNovels(response.userNovels)

            MediatorResult.Success(endOfPaginationReached = !response.isLoadable)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    companion object {
        private const val DEFAULT_LAST_USER_NOVEL_ID = 0L
    }
}
