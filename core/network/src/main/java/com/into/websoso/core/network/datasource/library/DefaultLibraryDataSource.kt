package com.into.websoso.core.network.datasource.library

import com.into.websoso.data.library.datasource.LibraryRemoteDataSource
import com.into.websoso.data.library.model.LibraryKeywordEntity
import com.into.websoso.data.library.model.UserNovelsEntity
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
        override suspend fun getUserNovels(
            userId: Long,
            cursor: String?,
            size: Int,
            sortType: String,
            isInterest: Boolean?,
            readStatuses: List<String>?,
            genres: List<String>?,
            isComplete: Boolean?,
            ratingMin: Float?,
            ratingMax: Float?,
            unratedOnly: Boolean?,
            attractivePoints: List<String>?,
            keywords: List<String>?,
        ): UserNovelsEntity =
            libraryApi
                .getUserNovels(
                    userId = userId,
                    size = size,
                    cursor = cursor,
                    sortType = sortType,
                    isInterest = isInterest,
                    readStatuses = readStatuses,
                    genres = genres,
                    isComplete = isComplete,
                    ratingMin = ratingMin,
                    ratingMax = ratingMax,
                    unratedOnly = unratedOnly,
                    attractivePoints = attractivePoints,
                    keywords = keywords,
                ).toData()

        override suspend fun getUserNovelKeywords(userId: Long): List<LibraryKeywordEntity> =
            libraryApi
                .getUserNovelKeywords(userId = userId)
                .toData()
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface LibraryDataSourceModule {
    @Binds
    @Singleton
    fun bindLibraryRemoteDataSource(defaultLibraryDataSource: DefaultLibraryDataSource): LibraryRemoteDataSource
}
