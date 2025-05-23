package com.into.websoso.core.network.datasource.library

import com.into.websoso.core.network.datasource.library.model.response.NovelResponseDto
import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.data.library.model.UserStorageEntity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

internal class DefaultLibraryDataSource
    @Inject
    constructor(
        private val libraryApi: LibraryApi,
    ) : LibraryRemoteDataSource {
        override suspend fun getUserLibrary(userId: Long): List<NovelEntity> =
            libraryApi.getUserLibrary(userId).userNovels.map(NovelResponseDto::toData)

        override suspend fun getUserLibrary(
            userId: Long,
            readStatus: String,
            lastUserNovelId: Long,
            size: Int,
            sortType: String,
        ): UserStorageEntity =
            libraryApi
                .getUserStorage(
                    userId,
                    readStatus,
                    lastUserNovelId,
                    size,
                    sortType,
                ).toData()

        override suspend fun getUserLibrary2(
            userNovelId: Int,
            size: Int,
        ): UserStorageEntity =
            libraryApi
                .getUserStorage(
                    userId = 184,
                    readStatus = "INTEREST",
                    lastUserNovelId = userNovelId.toLong(),
                    size = size,
                    sortType = "NEWEST",
                ).toData()
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface LibraryDataSourceModule {
    @Binds
    @Singleton
    fun bindLibraryRemoteDataSource(defaultLibraryDataSource: DefaultLibraryDataSource): LibraryRemoteDataSource
}
