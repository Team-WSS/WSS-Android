package com.into.websoso.data.library

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.UserStorageEntity
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
//        private val libraryRemoteMediator: LibraryRemoteMediator by lazy {
//            LibraryRemoteMediator(
//                libraryRemoteDataSource,
//                libraryLocalDataSource,
//            )
//        }

        fun getUserLibrary(): Flow<PagingData<NovelEntity>> =
            Pager(
                config = PagingConfig(
                    pageSize = NETWORK_PAGE_SIZE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = { LibraryPagingSource(libraryRemoteDataSource) },
            ).flow

//        @OptIn(ExperimentalPagingApi::class)
//        fun getUserLibrary(): Flow<PagingData<NovelEntity>> =
//            Pager(
//                config = PagingConfig(
//                    pageSize = 10,
//                    enablePlaceholders = false,
//                ),
//                remoteMediator = libraryRemoteMediator,
//                pagingSourceFactory = libraryLocalDataSource::selectAllNovels,
//            ).flow

        suspend fun getUserLibrary(userId: Long = 184): Result<Unit> =
            runCatching {
                val userLibrary = libraryRemoteDataSource.getUserLibrary(userId)
                libraryLocalDataSource.insertNovels(userLibrary)
            }

        suspend fun fetchUserStorage(
            userId: Long,
            readStatus: String,
            lastUserNovelId: Long,
            size: Int,
            sortType: String,
        ): Result<UserStorageEntity> =
            runCatching {
                libraryRemoteDataSource
                    .getUserLibrary(
                        userId = userId,
                        readStatus = readStatus,
                        lastUserNovelId = lastUserNovelId,
                        size = size,
                        sortType = sortType,
                    )
            }

        companion object {
            private const val NETWORK_PAGE_SIZE = 10
        }
    }
