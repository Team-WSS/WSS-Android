package com.teamwss.websoso.data.remote.api

import com.teamwss.websoso.data.model.OAuthToken

interface OAuthService {
    suspend fun login(): OAuthToken
    suspend fun logout()
    suspend fun withdraw()
}
