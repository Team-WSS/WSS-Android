package com.into.websoso.data.remote.api

import com.into.websoso.data.remote.response.AvatarsResponseDto
import retrofit2.http.GET

interface AvatarApi {
    @GET("avatars")
    suspend fun getAvatars(): AvatarsResponseDto
}
