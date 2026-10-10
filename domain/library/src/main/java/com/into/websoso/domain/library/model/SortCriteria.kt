package com.into.websoso.domain.library.model

enum class SortCriteria(
    val key: String,
    val label: String,
) {
    RECENT("created_desc", "등록 최신순"),
    OLD("created_asc", "등록 오래된순"),
    TITLE("title", "제목순"),
    READ_DATE("read_date", "날짜순"),
    RATING_HIGH("rating_desc", "별점 높은순"),
    RATING_LOW("rating_asc", "별점 낮은순"),
    ;

    companion object {
        fun from(value: String): SortCriteria = entries.find { it.key == value || it.name == value } ?: RECENT
    }
}
