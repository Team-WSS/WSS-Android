package com.into.websoso.data.remote.api

import com.into.websoso.data.model.OAuthToken

interface OAuthService {
    suspend fun login(): OAuthToken

    suspend fun logout()

    suspend fun withdraw()
}
