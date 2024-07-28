package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.response.NovelRatingResponseDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface UserNovelApi {

    @DELETE("user-novels/{novelId}")
    suspend fun deleteUserNovel(
        @Path("novelId") novelId: Long,
    )

    @GET("user-novels/{novelId}")
    suspend fun fetchUserNovel(
        @Path("novelId") novelId: Long,
    ): NovelRatingResponseDto
}
