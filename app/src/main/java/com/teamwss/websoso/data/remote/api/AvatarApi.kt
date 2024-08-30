package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.response.AvatarsResponseDto
import retrofit2.http.GET

interface AvatarApi {

    @GET("avatars")
    suspend fun getAvatars(): AvatarsResponseDto
}