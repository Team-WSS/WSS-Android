package com.into.websoso.data.library

import NovelRemoteMediator
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.into.websoso.core.database.entity.InDatabaseNovelEntity
import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.toData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository
    @Inject
    constructor(
        private val libraryRemoteDataSource: LibraryRemoteDataSource,
        private val libraryLocalDataSource: LibraryLocalDataSource,
    ) {
        @OptIn(ExperimentalPagingApi::class)
        fun getUserLibrary(
            userId: Long,
            lastUserNovelId: Long,
            size: Int,
            sortCriteria: String,
            isInterest: Boolean?,
            readStatuses: List<String>?,
            attractivePoints: List<String>?,
            novelRating: Float?,
            query: String?,
        ): Flow<PagingData<NovelEntity>> =
            Pager(
                config = PagingConfig(pageSize = NETWORK_PAGE_SIZE),
                remoteMediator = NovelRemoteMediator(
                    userId,
                    libraryRemoteDataSource,
                    libraryLocalDataSource,
                ),
            ) {
                libraryLocalDataSource.selectAllNovels()
            }.flow.map { it.map(InDatabaseNovelEntity::toData) }

        companion object {
            private const val NETWORK_PAGE_SIZE = 10
        }
    }
