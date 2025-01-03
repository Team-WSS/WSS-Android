package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.response.ExploreResultResponseDto
import com.into.websoso.data.remote.response.NovelDetailResponseDto
import com.into.websoso.data.remote.response.NovelFeedResponseDto
import com.into.websoso.data.remote.response.NovelInfoResponseDto
import com.into.websoso.data.remote.response.PopularNovelsResponseDto
import com.into.websoso.data.remote.response.RecommendedNovelsByUserTasteResponseDto
import com.into.websoso.data.remote.response.SosoPicksResponseDto
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

    @GET("novels/popular")
    suspend fun getPopularNovels(): PopularNovelsResponseDto

    @GET("novels/taste")
    suspend fun getRecommendedNovelsByUserTaste(): RecommendedNovelsByUserTasteResponseDto

    @GET("novels/filtered")
    suspend fun getFilteredNovelResult(
        @Query("genres") genres: List<String>?,
        @Query("isCompleted") isCompleted: Boolean?,
        @Query("novelRating") novelRating: Float?,
        @Query("keywordIds") keywordIds: List<Int>?,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): ExploreResultResponseDto

    @GET("novels/{novelId}/feeds")
    suspend fun getNovelFeeds(
        @Path("novelId") novelId: Long,
        @Query("lastFeedId") lastFeedId: Long,
        @Query("size") size: Int,
    ): NovelFeedResponseDto
}
