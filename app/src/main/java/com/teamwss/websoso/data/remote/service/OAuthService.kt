package com.teamwss.websoso.data.remote.service

import com.teamwss.websoso.data.model.OAuthToken

interface OAuthService {
    suspend fun login(): OAuthToken
    suspend fun logout()
    suspend fun withdraw()
}
