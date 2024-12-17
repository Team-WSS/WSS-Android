package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.response.MinimumVersionResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface VersionApi {
    @GET("minimum-version")
    suspend fun getMinimumVersion(
        @Query("os") os: String = "android",
    ): MinimumVersionResponseDto
}