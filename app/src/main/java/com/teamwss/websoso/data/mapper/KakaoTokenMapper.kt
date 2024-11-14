package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.KakaoOAuthToken

typealias KakaoToken = com.kakao.sdk.auth.model.OAuthToken

fun KakaoToken.toOAuthToken() = KakaoOAuthToken(
    accessToken = accessToken,
    refreshToken = refreshToken,
)