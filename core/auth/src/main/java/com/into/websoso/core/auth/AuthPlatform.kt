package com.into.websoso.core.auth

import dagger.MapKey

@MapKey(unwrapValue = true)
annotation class AuthPlatformKey(
    val value: AuthPlatform,
)

enum class AuthPlatform {
    KAKAO,
}
