package com.teamwss.websoso.ui.myPage.myActivity.model

enum class Genres(val korean: String) {
    ALL("전체"),
    ROMANCE("로맨스"),
    ROMANCEFANTASY("로판"),
    BOYSLOVE("BL"),
    FANTASY("판타지"),
    MODERN_FANTASY("현판"),
    WUXIA("무협"),
    DRAMA("드라마"),
    MYSTERY("미스터리"),
    LIGHTNOVEL("라노벨"),
    ETC("기타");

    companion object {
        fun fromString(value: String): Genres? {
            return Genres.values().find { it.name.equals(value, ignoreCase = true) }
        }
    }
}