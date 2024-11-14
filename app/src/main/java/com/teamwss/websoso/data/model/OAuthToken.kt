package com.teamwss.websoso.data.model

sealed interface OAuthToken {
    val accessToken: String
}

data class KakaoOAuthToken(
    override val accessToken: String,
    val refreshToken: String,
) : OAuthToken
