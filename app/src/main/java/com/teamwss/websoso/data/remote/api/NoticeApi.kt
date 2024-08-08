package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.response.NoticesResponseDto
import retrofit2.http.GET

interface NoticeApi {

    @GET("notices")
    suspend fun getNotices(): NoticesResponseDto
}