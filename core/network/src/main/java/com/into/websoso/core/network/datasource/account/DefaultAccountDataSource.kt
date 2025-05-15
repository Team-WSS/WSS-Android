package com.into.websoso.core.network.datasource.account

import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken
import com.into.websoso.core.network.datasource.account.model.KakaoLogoutRequestDto
import com.into.websoso.core.network.datasource.account.model.TokenReissueRequestDto
import com.into.websoso.data.account.datasource.AccountRemoteDataSource
import com.into.websoso.data.account.model.AccountEntity
import com.into.websoso.data.account.model.TokenEntity
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

internal class DefaultAccountDataSource
    @Inject
    constructor(
        private val accountApi: AccountApi,
    ) : AccountRemoteDataSource {
        override suspend fun postLogin(
            platform: AuthPlatform,
            authToken: AuthToken,
        ): AccountEntity =
            when (platform) {
                AuthPlatform.KAKAO ->
                    accountApi
                        .postLoginWithKakao(
                            accessToken = authToken.accessToken,
                        ).toData()
            }

        override suspend fun postLogout(
            refreshToken: String,
            deviceIdentifier: String,
        ) {
            accountApi.postLogoutWithKakao(
                kakaoLogoutRequestDto = KakaoLogoutRequestDto(
                    refreshToken = refreshToken,
                    deviceIdentifier = deviceIdentifier,
                ),
            )
        }

        override suspend fun postReissue(refreshToken: String): TokenEntity =
            accountApi
                .postReissueToken(
                    tokenReissueRequestDto = TokenReissueRequestDto(
                        refreshToken = refreshToken,
                    ),
                ).toData()
    }

@Module
@InstallIn(SingletonComponent::class)
internal interface AccountDataSourceModule {
    @Binds
    @Singleton
    fun bindAccountRemoteDataSource(defaultAccountDataSource: DefaultAccountDataSource): AccountRemoteDataSource
}
