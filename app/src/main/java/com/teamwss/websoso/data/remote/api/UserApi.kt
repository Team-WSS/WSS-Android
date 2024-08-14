package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.remote.response.UserEmailResponseDto
import retrofit2.http.GET

interface UserApi {

    @GET("users/email")
    suspend fun getUserEmail(): UserEmailResponseDto
}