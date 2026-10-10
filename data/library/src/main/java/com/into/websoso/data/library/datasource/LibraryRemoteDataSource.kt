package com.into.websoso.data.library.datasource

import com.into.websoso.data.library.model.LibraryKeywordEntity
import com.into.websoso.data.library.model.UserNovelsEntity

interface LibraryRemoteDataSource {
    suspend fun getUserNovelKeywords(userId: Long): List<LibraryKeywordEntity>

    suspend fun getUserNovels(
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
    ): UserNovelsEntity
}
