package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.NovelDetailEntity
import com.teamwss.websoso.data.remote.api.NovelApi
import javax.inject.Inject

class NovelRepository @Inject constructor(
    private val novelApi: NovelApi
) {

    suspend fun getNovelDetail(novelId: Long): NovelDetailEntity {
        return novelApi.getNovelDetail(novelId).toData()
    }
}
