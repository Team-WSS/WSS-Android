package com.into.websoso.data.library.datasource

import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.UserStorageEntity

interface LibraryRemoteDataSource {
    suspend fun getUserLibrary(userId: Long): List<NovelEntity>

    suspend fun getUserLibrary(
        userId: Long,
        readStatus: String,
        lastUserNovelId: Long,
        size: Int,
        sortType: String,
    ): UserStorageEntity
}
