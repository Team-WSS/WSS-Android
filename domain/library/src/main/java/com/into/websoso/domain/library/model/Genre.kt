package com.into.websoso.domain.library.model

enum class Genre(
    val key: String,
    val label: String,
) {
    FANTASY("fantasy", "판타지"),
    MODERN_FANTASY("modernFantasy", "현판"),
    ROMANCE("romance", "로맨스"),
    ROMANCE_FANTASY("romanceFantasy", "로판"),
    WUXIA("wuxia", "무협"),
    MYSTERY("mystery", "미스터리"),
    DRAMA("drama", "드라마"),
    LIGHT_NOVEL("lightNovel", "라노벨"),
    BOYS_LOVE("bl", "BL"),
    ;

    companion object {
        fun from(key: String): Genre? = entries.find { it.key == key }
    }
}
