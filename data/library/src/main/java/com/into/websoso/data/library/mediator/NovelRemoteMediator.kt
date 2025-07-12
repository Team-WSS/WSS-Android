package com.into.websoso.data.library.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.into.websoso.core.database.entity.InDatabaseNovelEntity
import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource

@OptIn(ExperimentalPagingApi::class)
class NovelRemoteMediator(
    private val userId: Long,
    private val libraryRemoteDataSource: LibraryRemoteDataSource,
    private val libraryLocalDataSource: LibraryLocalDataSource,
) : RemoteMediator<Int, InDatabaseNovelEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, InDatabaseNovelEntity>,
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
                isInterest = null,
                readStatuses = null,
                attractivePoints = null,
                novelRating = null,
                query = null,
                updatedSince = null,
            )
            if (loadType == LoadType.REFRESH) {
                libraryLocalDataSource.deleteAllNovels()
            }
            libraryLocalDataSource.insertNovels(response.userNovels)
            MediatorResult.Success(endOfPaginationReached = !response.isLoadable)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
