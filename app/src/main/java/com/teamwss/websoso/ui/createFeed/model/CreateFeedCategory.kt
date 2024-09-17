package com.teamwss.websoso.ui.createFeed.model

enum class CreateFeedCategory(
    val krTitle: String,
    val enTitle: String,
) {
    ROMANCE_FANTASY("로판", "romanceFantasy"),
    ROMANCE("로맨스", "romance"),
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

        fun from(title: String): CreateFeedCategory = CreateFeedCategory.entries.find { category ->
            title == category.krTitle
        } ?: throw IllegalArgumentException()

        fun from(index: Int): CreateFeedCategory = CreateFeedCategory.entries.find { category ->
            index == category.ordinal
        } ?: throw IllegalArgumentException()
    }
}
