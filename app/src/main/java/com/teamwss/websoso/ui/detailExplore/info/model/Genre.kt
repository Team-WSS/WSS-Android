package com.teamwss.websoso.ui.detailExplore.info.model

enum class Genre(
    val title: String,
) {
    ROMANCE("로맨스"),
    ROMANCE_FANTASY("로판"),
    FANTASY("판타지"),
    MODERN_FANTASY("현판"),
    WUXIA("무협"),
    MYSTERY("미스터리"),
    DRAMA("드라마"),
    LIGHT_NOVEL("라노벨"),
    BOYS_LOVE("BL");

    companion object {

        fun from(title: String): Genre {
            return entries.find { it.title == title } ?: throw IllegalArgumentException()
        }
    }
}