package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.response.NoticesResponseDto
import retrofit2.http.GET

interface NoticeApi {

    @GET("notices")
    suspend fun getNotices(): NoticesResponseDto
}