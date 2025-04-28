package com.into.websoso.data.mapper

import com.into.websoso.data.model.KakaoOAuthToken

typealias KakaoToken = com.kakao.sdk.auth.model.OAuthToken

fun KakaoToken.toOAuthToken() =
    KakaoOAuthToken(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
