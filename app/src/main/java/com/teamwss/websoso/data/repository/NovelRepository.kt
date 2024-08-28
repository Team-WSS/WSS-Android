package com.teamwss.websoso.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.ExploreResultEntity
import com.teamwss.websoso.data.model.ExploreResultEntity.NovelEntity
import com.teamwss.websoso.data.model.NovelDetailEntity
import com.teamwss.websoso.data.model.NovelInfoEntity
import com.teamwss.websoso.data.model.PopularNovelsEntity
import com.teamwss.websoso.data.model.RecommendedNovelsByUserTasteEntity
import com.teamwss.websoso.data.model.SosoPickEntity
import com.teamwss.websoso.data.remote.api.NovelApi
import javax.inject.Inject

class NovelRepository @Inject constructor(
    private val novelApi: NovelApi,
) {
    private val _cachedNormalExploreIsLoadable: MutableLiveData<Boolean> = MutableLiveData()
    val cachedNormalExploreIsLoadable: LiveData<Boolean> get() = _cachedNormalExploreIsLoadable

    private val _cachedNormalExploreResult: MutableList<NovelEntity> = mutableListOf()
    val cachedNormalExploreResult: List<NovelEntity> get() = _cachedNormalExploreResult.toList()

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
                _cachedNormalExploreIsLoadable.value = result.isLoadable
            }
            .copy(
                isLoadable = cachedNormalExploreIsLoadable.value ?: true,
                novels = cachedNormalExploreResult,
            )
    }

    fun clearCachedNormalExploreResult() {
        if (cachedNormalExploreResult.isNotEmpty()) _cachedNormalExploreResult.clear()
    }

    suspend fun fetchPopularNovels(): PopularNovelsEntity {
        return novelApi.getPopularNovels().toData()
    }

    suspend fun fetchRecommendedNovelsByUserTaste(): RecommendedNovelsByUserTasteEntity {
        return novelApi.getRecommendedNovelsByUserTaste().toData()
    }
}
