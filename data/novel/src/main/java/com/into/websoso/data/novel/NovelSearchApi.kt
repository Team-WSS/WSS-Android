package com.into.websoso.data.novel

import retrofit2.http.GET
import retrofit2.http.Query

internal interface NovelSearchApi {
    @GET("novels")
    suspend fun getNovels(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): NovelSearchResponseDto
}
