package com.teamwss.websoso.ui.feed.model

enum class Category(val title: String) {
    ALL("전체"),
    ROMANCE("로맨스"),
    ROMANCE_FANTASY("로판"),
    BOYS_LOVE("BL"),
    FANTASY("판타지"),
    MODERN_FANTASY("현판"),
    WUXIA("무협"),
    DRAMA("드라마"),
    MYSTERY("미스터리"),
    LIGHT_NOVEL("라노벨"),
    ETC("기타"),
    ;

    companion object {

        fun from(title: String): Category = Category.entries.find { category ->
            title == category.title
        } ?: throw IllegalArgumentException()
    }
}
