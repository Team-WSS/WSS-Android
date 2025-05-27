package com.into.websoso.data.library

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.NovelEntity

@OptIn(ExperimentalPagingApi::class)
internal class LibraryRemoteMediator(
    private val libraryRemoteDataSource: LibraryRemoteDataSource,
    private val libraryLocalDataSource: LibraryLocalDataSource,
) : RemoteMediator<Long, NovelEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Long, NovelEntity>,
    ): MediatorResult {
        try {
            val lastNovelId = when (loadType) {
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.REFRESH -> 0L
                LoadType.APPEND -> state.lastItemOrNull()?.novelId ?: return MediatorResult.Success(
                    endOfPaginationReached = true,
                )
            }
            val userLibrary = libraryRemoteDataSource.getUserLibrary2(userNovelId = lastNovelId)

            if (loadType == LoadType.REFRESH) libraryLocalDataSource.deleteAllNovels()

            libraryLocalDataSource.insertNovels(novels = userLibrary.userNovels)

            return MediatorResult.Success(
                endOfPaginationReached = !userLibrary.isLoadable,
            )
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}
