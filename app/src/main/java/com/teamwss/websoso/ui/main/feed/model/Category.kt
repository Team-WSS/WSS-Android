package com.teamwss.websoso.ui.main.feed.model

enum class Category(
    val krTitle: String,
    val enTitle: String,
) {
    ALL("전체", "all"),
    ROMANCE("로맨스", "romance"),
    ROMANCE_FANTASY("로판", "romanceFantasy"),
    BOYS_LOVE("BL", "BL"),
    FANTASY("판타지", "fantasy"),
    MODERN_FANTASY("현판", "modernFantasy"),
    WUXIA("무협", "wuxia"),
    DRAMA("드라마", "drama"),
    MYSTERY("미스터리", "mystery"),
    LIGHT_NOVEL("라노벨", "lightNovel"),
    ETC("기타", "etc")
    ;

    companion object {

        fun from(title: String): Category = Category.entries.find { category ->
            title == category.krTitle
        } ?: throw IllegalArgumentException()
    }
}
