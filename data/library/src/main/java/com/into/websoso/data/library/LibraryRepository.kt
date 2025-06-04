package com.into.websoso.data.library

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.NovelEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository
    @Inject
    constructor(
        private val libraryRemoteDataSource: LibraryRemoteDataSource,
        private val libraryLocalDataSource: LibraryLocalDataSource,
    ) {
        fun getUserLibrary(
            userId: Long,
            lastUserNovelId: Long,
            size: Int,
            sortType: String,
            isInterest: Boolean?,
            readStatuses: List<String>?,
            attractivePoints: List<String>?,
            novelRating: Float?,
            query: String?,
        ): Flow<PagingData<NovelEntity>> =
            Pager(
                config = PagingConfig(
                    pageSize = NETWORK_PAGE_SIZE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    LibraryPagingSource(
                        libraryRemoteDataSource = libraryRemoteDataSource,
                        userId = userId,
                        lastUserNovelId = lastUserNovelId,
                        size = size,
                        sortType = sortType,
                        isInterest = isInterest,
                        readStatuses = readStatuses,
                        attractivePoints = attractivePoints,
                        novelRating = novelRating,
                        query = query,
                    )
                },
            ).flow

        companion object {
            private const val NETWORK_PAGE_SIZE = 10
        }
    }
