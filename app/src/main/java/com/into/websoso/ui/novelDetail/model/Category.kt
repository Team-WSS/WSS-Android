package com.into.websoso.ui.novelDetail.model

enum class Category(
    val titleKr: String,
    val titleEn: String,
) {
    ROMANCE("로맨스", "romance"),
    ROMANCE_FANTASY("로판", "romanceFantasy"),
    BOYS_LOVE("BL", "bl"),
    FANTASY("판타지", "fantasy"),
    MODERN_FANTASY("현판", "modernFantasy"),
    WUXIA("무협", "wuxia"),
    DRAMA("드라마", "drama"),
    MYSTERY("미스터리", "mystery"),
    LIGHT_NOVEL("라노벨", "lightNovel"),
    ;

    companion object {

        fun from(title: String): Category = Category.entries.find { category ->
            title == category.titleEn || title == category.titleKr
        } ?: throw IllegalArgumentException()
    }
}
