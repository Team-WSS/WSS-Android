package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.response.NovelDetailResponseDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NovelApi {

    @GET("novels/{novelId}")
    suspend fun getNovelDetail(
        @Path("novelId") novelId: Long,
    ): NovelDetailResponseDto

    @POST("novels/{novelId}/is-interest")
    suspend fun postUserInterest(
        @Path("novelId") novelId: Long,
    ): Response<Unit>

    @DELETE("novels/{novelId}/is-interest")
    suspend fun deleteUserInterest(
        @Path("novelId") novelId: Long,
    ): Response<Unit>
}
