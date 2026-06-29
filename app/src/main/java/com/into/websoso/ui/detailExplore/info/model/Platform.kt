package com.into.websoso.ui.detailExplore.info.model

enum class Platform(
    val displayName: String,
    val apiName: String,
    val chipWidthDp: Int,
) {
    KAKAO_PAGE("카카오페이지", "카카오페이지", 101),
    NAVER_SERIES("네이버시리즈", "네이버시리즈", 101),
    NOVELPIA("노벨피아", "노벨피아", 77),
    RIDI("리디", "리디북스", 52),
    MUNPIA("문피아", "문피아", 64),
}
