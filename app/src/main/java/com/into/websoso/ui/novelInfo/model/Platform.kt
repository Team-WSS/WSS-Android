package com.into.websoso.ui.novelInfo.model

enum class Platform(val platformName: String) {
    NAVER_SERIES("네이버시리즈"),
    KAKAO_PAGE("카카오페이지"),
    INVALID_PLATFORM(""),
    ;

    companion object {
        fun fromPlatformName(platformName: String): Platform {
            return entries.firstOrNull { it.platformName == platformName } ?: INVALID_PLATFORM
        }
    }
}
