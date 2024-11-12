package com.teamwss.websoso.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.teamwss.websoso.data.mapper.toData
import com.teamwss.websoso.data.model.LoginEntity
import com.teamwss.websoso.data.remote.api.AuthApi
import com.teamwss.websoso.data.remote.request.LogoutRequestDto
import com.teamwss.websoso.data.remote.request.TokenReissueRequestDto
import com.teamwss.websoso.data.remote.request.UserProfileRequestDto
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val authStorage: DataStore<Preferences>,
) {

    suspend fun loginWithKakao(accessToken: String): LoginEntity {
        val response = authApi.loginWithKakao(accessToken)
        saveAccessToken(response.authorization)
        saveRefreshToken(response.refreshToken)
        return response.toData()
    }

    suspend fun fetchNicknameValidity(authorization: String, nickname: String): Boolean {
        return authApi.getNicknameValidity("Bearer $authorization", nickname).isValid
    }

    suspend fun signUp(
        authorization: String,
        nickname: String,
        gender: String,
        birth: Int,
        genrePreferences: List<String>,
    ) {
        authApi.postUserProfile(
            "Bearer $authorization",
            UserProfileRequestDto(
                nickname,
                gender,
                birth,
                genrePreferences,
            )
        )
    }

    suspend fun logout() {
        runCatching {
            val refreshToken = fetchRefreshToken()
            val accessToken = fetchAccessToken()
            if (accessToken.isNotEmpty() && refreshToken.isNotEmpty()) {
                val logoutRequest = LogoutRequestDto(refreshToken)
                authApi.logout("Bearer $accessToken", logoutRequest)
            }
        }.onSuccess {
            clearTokens()
        }.onFailure {
            it.printStackTrace()
        }
    }

    suspend fun isAutoLoginConfigured(): Boolean {
        return authStorage.data.first()[AUTO_LOGIN_CONFIGURED_KEY] ?: false
    }

    suspend fun setAutoLoginConfigured(configured: Boolean) {
        authStorage.edit { preferences ->
            preferences[AUTO_LOGIN_CONFIGURED_KEY] = configured
        }
    }

    suspend fun fetchAccessToken(): String {
        return authStorage.data.first()[ACCESS_TOKEN_KEY].orEmpty()
    }

    suspend fun fetchRefreshToken(): String {
        return authStorage.data.first()[REFRESH_TOKEN_KEY].orEmpty()
    }

    suspend fun saveAccessToken(token: String) {
        authStorage.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    suspend fun saveRefreshToken(token: String) {
        authStorage.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = token
        }
    }

    suspend fun clearTokens() {
        authStorage.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
        }
    }

    suspend fun reissueToken(refreshToken: String): String? {
        val reissueRequest = TokenReissueRequestDto(refreshToken)
        return try {
            val response = authApi.reissueToken(reissueRequest)
            saveAccessToken(response.authorization)
            saveRefreshToken(response.refreshToken)
            response.authorization
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("ACCESS_TOKEN")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("REFRESH_TOKEN")
        private val AUTO_LOGIN_CONFIGURED_KEY = booleanPreferencesKey("AUTO_LOGIN_CONFIGURED")
    }
}
