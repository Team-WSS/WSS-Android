package com.teamwss.websoso.data.remote.api

import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Path

interface UserNovelApi {

    @DELETE("user-novels/{novelId}")
    suspend fun deleteUserNovel(
        @Path("novelId") novelId: Long,
    ): Response<Unit>
}