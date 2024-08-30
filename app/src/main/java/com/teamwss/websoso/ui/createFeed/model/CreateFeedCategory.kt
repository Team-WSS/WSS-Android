package com.teamwss.websoso.ui.createFeed.model

enum class CreateFeedCategory(
    val titleKr: String,
    val titleEn: String,
) {
    ROMANCE_FANTASY("로판", "romanceFantasy"),
    ROMANCE("로맨스", "romance"),
    BOYS_LOVE("BL", "bl"),
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
            title == category.titleKr
        } ?: throw IllegalArgumentException()
    }
}
