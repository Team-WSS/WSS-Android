package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.response.KeywordsResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface KeywordApi {

    @GET("keywords")
    suspend fun getKeywords(
        @Query("keyword") keyword: String?,
    ): KeywordsResponseDto
}