package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.response.UserEmailResponseDto
import com.teamwss.websoso.data.remote.response.UserNovelStatsResponseDto
import retrofit2.http.GET

interface UserApi {

    @GET("users/email")
    suspend fun getUserEmail(): UserEmailResponseDto

    @GET("users/user-novel-stats")
    suspend fun getUserNovelStats(): UserNovelStatsResponseDto
}