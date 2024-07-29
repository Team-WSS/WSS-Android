package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.request.NovelRatingRequestDto
import com.teamwss.websoso.data.remote.response.NovelRatingResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserNovelApi {

    @DELETE("user-novels/{novelId}")
    suspend fun deleteUserNovel(
        @Path("novelId") novelId: Long,
    )

    @GET("user-novels/{novelId}")
    suspend fun fetchNovelRating(
        @Path("novelId") novelId: Long,
    ): NovelRatingResponseDto
}
