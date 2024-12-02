package com.into.websoso.ui.createFeed.model

enum class CreateFeedCategory(
    val krTitle: String,
    val enTitle: String,
) {

    FANTASY("판타지", "fantasy"),
    MODERN_FANTASY("현판", "modernFantasy"),
    ROMANCE("로맨스", "romance"),
    ROMANCE_FANTASY("로판", "romanceFantasy"),
    WUXIA("무협", "wuxia"),
    DRAMA("드라마", "drama"),
    MYSTERY("미스터리", "mystery"),
    LIGHT_NOVEL("라노벨", "lightNovel"),
    BOYS_LOVE("BL", "BL"),
    ETC("기타", "etc")
    ;

    companion object {

        fun from(title: String): CreateFeedCategory = CreateFeedCategory.entries.find { category ->
            title == category.krTitle
        } ?: throw IllegalArgumentException()

        fun from(index: Int): CreateFeedCategory = CreateFeedCategory.entries.find { category ->
            index == category.ordinal
        } ?: throw IllegalArgumentException()
    }
}
