package com.into.websoso.core.network.datasource.user

import com.into.websoso.core.network.datasource.user.model.UserFeedsResponseDto
import com.into.websoso.core.network.datasource.user.model.UserInfoResponseDto
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

interface UserApi {
    @GET("users/{userId}/feeds")
    suspend fun getUserFeeds(
        @Path("userId") userId: Long,
        @Query("lastFeedId") lastFeedId: Long,
        @Query("size") size: Int,
    ): UserFeedsResponseDto

    @GET("users/me")
    suspend fun getUserInfo(): UserInfoResponseDto
}

@Module
@InstallIn(SingletonComponent::class)
internal object UserApiModule {
    @Provides
    @Singleton
    internal fun provideUserApi(retrofit: Retrofit): UserApi = retrofit.create(UserApi::class.java)
}
