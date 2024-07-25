package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.NovelDetailEntity
import com.teamwss.websoso.data.model.NovelInfoEntity
import com.teamwss.websoso.data.remote.api.NovelApi
import javax.inject.Inject

class NovelRepository @Inject constructor(
    private val novelApi: NovelApi,
) {

    suspend fun getNovelDetail(novelId: Long): NovelDetailEntity {
        return novelApi.getNovelDetail(novelId).toData()
    }

    suspend fun postUserInterest(novelId: Long) {
        novelApi.postUserInterest(novelId)
    }

    suspend fun deleteUserInterest(novelId: Long) {
        novelApi.deleteUserInterest(novelId)
    }

    suspend fun getNovelInfo(novelId: Long): NovelInfoEntity {
        return novelApi.getNovelInfo(novelId).toData()
    }
}
