package com.into.websoso.data.repository

import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.CategoriesEntity
import com.into.websoso.data.remote.api.KeywordApi
import javax.inject.Inject

class KeywordRepository
    @Inject
    constructor(
        private val keywordApi: KeywordApi,
    ) {
        suspend fun fetchKeywords(keyword: String?): CategoriesEntity = keywordApi.getKeywords(keyword).toData()
    }
