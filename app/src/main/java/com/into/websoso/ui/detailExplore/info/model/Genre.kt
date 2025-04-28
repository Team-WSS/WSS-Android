package com.into.websoso.ui.detailExplore.info.model

enum class Genre(
    val titleKr: String,
    val titleEn: String,
) {
    ROMANCE("로맨스", "romance"),
    ROMANCE_FANTASY("로판", "romanceFantasy"),
    FANTASY("판타지", "fantasy"),
    MODERN_FANTASY("현판", "modernFantasy"),
    WUXIA("무협", "wuxia"),
    MYSTERY("미스터리", "mystery"),
    DRAMA("드라마", "drama"),
    LIGHT_NOVEL("라노벨", "lightNovel"),
    BOYS_LOVE("BL", "bl"),
    ;

    companion object {
        fun from(title: String): Genre = entries.find { it.titleKr == title } ?: throw IllegalArgumentException()
    }
}
