package com.into.websoso.data.repository

import com.into.websoso.data.remote.api.AuthApi
import com.into.websoso.data.remote.request.FCMTokenRequestDto
import com.into.websoso.data.remote.request.UserProfileRequestDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository
    @Inject
    constructor(
        private val authApi: AuthApi,
    ) {
        suspend fun fetchNicknameValidity(nickname: String): Boolean = authApi.getNicknameValidity(nickname).isValid

        suspend fun signUp(
            nickname: String,
            gender: String,
            birth: Int,
            genrePreferences: List<String>,
        ) {
            authApi.postUserProfile(
                UserProfileRequestDto(nickname, gender, birth, genrePreferences),
            )
        }

        suspend fun saveFCMToken(
            fcmToken: String,
            deviceIdentifier: String,
        ) {
            authApi.postFCMToken(
                FCMTokenRequestDto(
                    fcmToken = fcmToken,
                    deviceIdentifier = deviceIdentifier,
                ),
            )
        }
    }
