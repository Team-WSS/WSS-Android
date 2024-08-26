package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.response.ExploreResultResponseDto
import com.teamwss.websoso.data.remote.response.NovelDetailResponseDto
import com.teamwss.websoso.data.remote.response.NovelInfoResponseDto
import com.teamwss.websoso.data.remote.response.SosoPicksResponseDto
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NovelApi {

    @GET("novels/{novelId}")
    suspend fun getNovelDetail(
        @Path("novelId") novelId: Long,
    ): NovelDetailResponseDto

    @POST("novels/{novelId}/is-interest")
    suspend fun postUserInterest(
        @Path("novelId") novelId: Long,
    )

    @DELETE("novels/{novelId}/is-interest")
    suspend fun deleteUserInterest(
        @Path("novelId") novelId: Long,
    )

    @GET("novels/{novelId}/info")
    suspend fun getNovelInfo(
        @Path("novelId") novelId: Long,
    ): NovelInfoResponseDto

    @GET("soso-picks")
    suspend fun getSosoPicks(): SosoPicksResponseDto

    @GET("novels")
    suspend fun getNormalExploreResult(
        @Query("query") searchWord: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ExploreResultResponseDto
}
