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
                val lastItem = state.lastItemOrNull()
                lastItem?.userNovelId ?: return MediatorResult.Success(true)
            }
        }

        return try {
            val response = libraryRemoteDataSource.getUserNovels(
                userId = userId,
                lastUserNovelId = lastUserNovelId ?: 0,
                size = state.config.pageSize,
                sortCriteria = "RECENT",
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
}
