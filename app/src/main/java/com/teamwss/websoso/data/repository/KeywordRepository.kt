package com.teamwss.websoso.data.repository

import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.KeywordsEntity
import com.teamwss.websoso.data.remote.api.KeywordApi
import javax.inject.Inject

class KeywordRepository @Inject constructor(
    private val keywordApi: KeywordApi,
) {

    suspend fun fetchKeywords(keyword: String?): KeywordsEntity {
        return keywordApi.getKeywords(keyword).toData()
    }
}