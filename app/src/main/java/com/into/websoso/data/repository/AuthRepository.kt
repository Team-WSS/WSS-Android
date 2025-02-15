package com.into.websoso.data.repository

import android.content.SharedPreferences
import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.LoginEntity
import com.into.websoso.data.remote.api.AuthApi
import com.into.websoso.data.remote.request.FCMTokenRequestDto
import com.into.websoso.data.remote.request.LogoutRequestDto
import com.into.websoso.data.remote.request.TokenReissueRequestDto
import com.into.websoso.data.remote.request.UserProfileRequestDto
import com.into.websoso.data.remote.request.WithdrawRequestDto
import javax.inject.Inject

class AuthRepository
    @Inject
    constructor(
        private val authApi: AuthApi,
        private val preferences: SharedPreferences,
    ) {
        var accessToken: String
            get() = preferences.getString(ACCESS_TOKEN_KEY, "").orEmpty()
            private set(value) = preferences.edit().putString(ACCESS_TOKEN_KEY, value).apply()

        var refreshToken: String
            get() = preferences.getString(REFRESH_TOKEN_KEY, "").orEmpty()
            private set(value) = preferences.edit().putString(REFRESH_TOKEN_KEY, value).apply()

        var isAutoLogin: Boolean
            get() = preferences.getBoolean(AUTO_LOGIN_KEY, false)
            private set(value) = preferences.edit().putBoolean(AUTO_LOGIN_KEY, value).apply()

        suspend fun loginWithKakao(accessToken: String): LoginEntity {
            val response = authApi.loginWithKakao(accessToken)
            this.accessToken = response.authorization
            this.refreshToken = response.refreshToken
            return response.toData()
        }

        suspend fun fetchNicknameValidity(
            authorization: String,
            nickname: String,
        ): Boolean = authApi.getNicknameValidity("Bearer $authorization", nickname).isValid

        suspend fun signUp(
            authorization: String,
            nickname: String,
            gender: String,
            birth: Int,
            genrePreferences: List<String>,
        ) {
            authApi.postUserProfile(
                "Bearer $authorization",
                UserProfileRequestDto(nickname, gender, birth, genrePreferences),
            )
        }

        suspend fun logout(deviceIdentifier: String) {
            runCatching {
                if (accessToken.isNotEmpty() && refreshToken.isNotEmpty()) {
                    authApi.logout(
                        "Bearer $accessToken",
                        LogoutRequestDto(refreshToken, deviceIdentifier),
                    )
                }
            }.onSuccess {
                clearTokens()
            }.onFailure {
                it.printStackTrace()
            }
        }

        suspend fun withdraw(reason: String) {
            runCatching {
                if (accessToken.isNotEmpty() && refreshToken.isNotEmpty()) {
                    authApi.withdraw("Bearer $accessToken", WithdrawRequestDto(reason, refreshToken))
                }
            }.onSuccess {
                clearTokens()
            }.onFailure {
                it.printStackTrace()
            }
        }

        fun clearTokens() {
            preferences.edit().apply {
                remove(ACCESS_TOKEN_KEY)
                remove(REFRESH_TOKEN_KEY)
                apply()
            }
        }

        suspend fun reissueToken(): String? =
            runCatching {
                val response = authApi.reissueToken(TokenReissueRequestDto(refreshToken))
                accessToken = response.authorization
                refreshToken = response.refreshToken
                response.authorization
            }.getOrElse {
                it.printStackTrace()
                null
            }

        fun updateAccessToken(accessToken: String) {
            this.accessToken = accessToken
        }

        fun updateRefreshToken(refreshToken: String) {
            this.refreshToken = refreshToken
        }

        fun updateIsAutoLogin(isAutoLogin: Boolean) {
            this.isAutoLogin = isAutoLogin
        }

        suspend fun saveFCMToken(
            fcmToken: String,
            deviceIdentifier: String,
        ) {
            authApi.postFCMToken(
                authorization = "Bearer $accessToken",
                FCMTokenRequestDto(
                    fcmToken = fcmToken,
                    deviceIdentifier = deviceIdentifier,
                ),
            )
        }

        companion object {
            private const val ACCESS_TOKEN_KEY = "ACCESS_TOKEN"
            private const val REFRESH_TOKEN_KEY = "REFRESH_TOKEN"
            private const val AUTO_LOGIN_KEY = "AUTO_LOGIN"
        }
    }
