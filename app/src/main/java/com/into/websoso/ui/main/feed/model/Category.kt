package com.into.websoso.ui.main.feed.model

enum class Category(
    val krTitle: String,
    val enTitle: String,
    val shortCode: String,
) {
    ALL("전체", "all", "all"),
    ROMANCE("로맨스", "romance", "R"),
    ROMANCE_FANTASY("로판", "romanceFantasy", "RF"),
    BOYS_LOVE("BL", "BL", "BL"),
    FANTASY("판타지", "fantasy", "F"),
    MODERN_FANTASY("현판", "modernFantasy", "HF"),
    WUXIA("무협", "wuxia", "MH"),
    DRAMA("드라마", "drama", "D"),
    MYSTERY("미스터리", "mystery", "M"),
    LIGHT_NOVEL("라노벨", "lightNovel", "LN"),
    ETC("기타", "etc", "etc");
    ;

    companion object {

        fun from(title: String): Category = Category.entries.find { category ->
            title == category.krTitle
        } ?: throw IllegalArgumentException()
    }
}
