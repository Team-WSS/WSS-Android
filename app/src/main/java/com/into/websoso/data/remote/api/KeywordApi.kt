package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.response.KeywordsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface KeywordApi {
    @GET("keywords")
    suspend fun getKeywords(
        @Query("query") keyword: String?,
    ): KeywordsResponseDto
}
