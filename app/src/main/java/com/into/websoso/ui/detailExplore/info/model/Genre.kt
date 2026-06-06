package com.into.websoso.ui.detailExplore.info.model

enum class Genre(
    val titleKr: String,
    val titleEn: String,
    val figmaWidthDp: Int,
) {
    ROMANCE("로맨스", "romance", 64),
    ROMANCE_FANTASY("로판", "romanceFantasy", 52),
    FANTASY("판타지", "fantasy", 64),
    MODERN_FANTASY("현판", "modernFantasy", 52),
    WUXIA("무협", "wuxia", 52),
    MYSTERY("미스터리", "mystery", 77),
    DRAMA("드라마", "drama", 64),
    LIGHT_NOVEL("라노벨", "lightNovel", 64),
    BOYS_LOVE("BL", "bl", 43),
    ;

    companion object {
        fun from(title: String): Genre = entries.find { it.titleKr == title } ?: throw IllegalArgumentException()
    }
}
