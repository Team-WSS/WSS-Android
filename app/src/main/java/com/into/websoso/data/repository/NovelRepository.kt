package com.into.websoso.data.repository

import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.ExploreResultEntity
import com.into.websoso.data.model.ExploreResultEntity.NovelEntity
import com.into.websoso.data.model.NovelDetailEntity
import com.into.websoso.data.model.NovelFeedsEntity
import com.into.websoso.data.model.NovelInfoEntity
import com.into.websoso.data.model.PopularNovelsEntity
import com.into.websoso.data.model.RecommendedNovelsByUserTasteEntity
import com.into.websoso.data.model.SosoPickEntity
import com.into.websoso.data.remote.api.NovelApi
import javax.inject.Inject

class NovelRepository @Inject constructor(
    private val novelApi: NovelApi,
) {
    var cachedNormalExploreIsLoadable: Boolean = true
        private set

    private val _cachedNormalExploreResult: MutableList<NovelEntity> = mutableListOf()
    val cachedNormalExploreResult: List<NovelEntity> get() = _cachedNormalExploreResult.toList()

    var cachedDetailExploreIsLoadable: Boolean = true
        private set

    private val _cachedDetailExploreResult: MutableList<NovelEntity> = mutableListOf()
    val cachedDetailExploreResult: List<NovelEntity> get() = _cachedDetailExploreResult.toList()

    suspend fun getNovelDetail(novelId: Long): NovelDetailEntity {
        return novelApi.getNovelDetail(novelId).toData()
    }

    suspend fun saveUserInterest(novelId: Long, isInterest: Boolean) {
        when (isInterest) {
            true -> novelApi.postUserInterest(novelId)
            false -> novelApi.deleteUserInterest(novelId)
        }
    }

    suspend fun fetchNovelInfo(novelId: Long): NovelInfoEntity {
        return novelApi.getNovelInfo(novelId).toData()
    }

    suspend fun fetchSosoPicks(): SosoPickEntity {
        return novelApi.getSosoPicks().toData()
    }

    suspend fun fetchNormalExploreResult(
        searchWord: String,
        page: Int,
        size: Int,
    ): ExploreResultEntity {
        val result =
            novelApi.getNormalExploreResult(searchWord = searchWord, page = page, size = size)

        return result.toData()
            .also {
                _cachedNormalExploreResult.addAll(it.novels)
                cachedNormalExploreIsLoadable = result.isLoadable
            }
            .copy(
                isLoadable = cachedNormalExploreIsLoadable,
                novels = cachedNormalExploreResult,
            )
    }

    fun clearCachedNormalExploreResult() {
        _cachedNormalExploreResult.clear()
        cachedNormalExploreIsLoadable = true
    }

    suspend fun fetchPopularNovels(): PopularNovelsEntity {
        return novelApi.getPopularNovels().toData()
    }

    suspend fun fetchRecommendedNovelsByUserTaste(): RecommendedNovelsByUserTasteEntity {
        return novelApi.getRecommendedNovelsByUserTaste().toData()
    }

    suspend fun fetchFilteredNovelResult(
        genres: List<String>?,
        isCompleted: Boolean?,
        novelRating: Float?,
        keywordIds: List<Int>?,
        page: Int,
        size: Int,
    ): ExploreResultEntity {
        val result = novelApi.getFilteredNovelResult(
            genres = genres,
            isCompleted = isCompleted,
            novelRating = novelRating,
            keywordIds = keywordIds,
            page = page,
            size = size,
        )

        return result.toData()
            .also {
                _cachedDetailExploreResult.addAll(it.novels)
                cachedDetailExploreIsLoadable = result.isLoadable
            }
            .copy(
                isLoadable = cachedDetailExploreIsLoadable,
                novels = cachedDetailExploreResult,
            )
    }

    fun clearCachedDetailExploreResult() {
        _cachedDetailExploreResult.clear()
        cachedDetailExploreIsLoadable = true
    }

    suspend fun fetchNovelFeeds(novelId: Long, lastFeedId: Long, size: Int): NovelFeedsEntity {
        return novelApi.getNovelFeeds(novelId, lastFeedId, size).toData()
    }
}
