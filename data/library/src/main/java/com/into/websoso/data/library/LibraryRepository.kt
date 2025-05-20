package com.into.websoso.data.library

import com.into.websoso.data.library.datasource.LibraryLocalDataSource
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.UserStorageEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepository
    @Inject
    constructor(
        private val libraryRemoteDataSource: LibraryRemoteDataSource,
        private val libraryLocalDataSource: LibraryLocalDataSource,
    ) {
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
    }
