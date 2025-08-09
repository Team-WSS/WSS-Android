package com.into.websoso.data.repository

import com.into.websoso.data.library.datasource.LibraryLocalDataSource
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

class NovelRepository
    @Inject
    constructor(
        private val novelApi: NovelApi,
        private val libraryLocalDataSource: LibraryLocalDataSource,
    ) {
        var cachedNormalExploreIsLoadable: Boolean = true
            private set

        private val _cachedNormalExploreResult: MutableList<NovelEntity> = mutableListOf()
        val cachedNormalExploreResult: List<NovelEntity> get() = _cachedNormalExploreResult.toList()

        var cachedDetailExploreIsLoadable: Boolean = true
            private set

        private val _cachedDetailExploreResult: MutableList<NovelEntity> = mutableListOf()
        val cachedDetailExploreResult: List<NovelEntity> get() = _cachedDetailExploreResult.toList()

        suspend fun getNovelDetail(novelId: Long): NovelDetailEntity = novelApi.getNovelDetail(novelId).toData()

        suspend fun saveUserInterest(
            novelId: Long,
            isInterest: Boolean,
        ) {
            runCatching {
                when (isInterest) {
                    true -> novelApi.postUserInterest(novelId)
                    false -> novelApi.deleteUserInterest(novelId)
                }
            }.onSuccess {
                libraryLocalDataSource.selectNovelByNovelId(novelId)?.let { novel ->
                    libraryLocalDataSource.insertNovel(novel.copy(isInterest = isInterest))
                }
            }
        }

        suspend fun fetchNovelInfo(novelId: Long): NovelInfoEntity = novelApi.getNovelInfo(novelId).toData()

        suspend fun fetchSosoPicks(): SosoPickEntity = novelApi.getSosoPicks().toData()

        suspend fun fetchNormalExploreResult(
            searchWord: String,
            page: Int,
            size: Int,
        ): ExploreResultEntity {
            val result =
                novelApi.getNormalExploreResult(searchWord = searchWord, page = page, size = size)

            return result
                .toData()
                .also {
                    _cachedNormalExploreResult.addAll(it.novels)
                    cachedNormalExploreIsLoadable = result.isLoadable
                }.copy(
                    isLoadable = cachedNormalExploreIsLoadable,
                    novels = cachedNormalExploreResult,
                )
        }

        fun clearCachedNormalExploreResult() {
            _cachedNormalExploreResult.clear()
            cachedNormalExploreIsLoadable = true
        }

        suspend fun fetchPopularNovels(): PopularNovelsEntity = novelApi.getPopularNovels().toData()

        suspend fun fetchRecommendedNovelsByUserTaste(): RecommendedNovelsByUserTasteEntity =
            novelApi.getRecommendedNovelsByUserTaste().toData()

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

            return result
                .toData()
                .also {
                    _cachedDetailExploreResult.addAll(it.novels)
                    cachedDetailExploreIsLoadable = result.isLoadable
                }.copy(
                    isLoadable = cachedDetailExploreIsLoadable,
                    novels = cachedDetailExploreResult,
                )
        }

        fun clearCachedDetailExploreResult() {
            _cachedDetailExploreResult.clear()
            cachedDetailExploreIsLoadable = true
        }

        suspend fun fetchNovelFeeds(
            novelId: Long,
            lastFeedId: Long,
            size: Int,
        ): NovelFeedsEntity = novelApi.getNovelFeeds(novelId, lastFeedId, size).toData()
    }
