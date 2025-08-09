package com.into.websoso.domain.library.model

enum class SortCriteria(
    val key: String,
    val label: String,
) {
    RECENT("RECENT", "최신 순"),
    OLD("OLD", "오래된 순"),
    ;

    companion object {
        fun from(key: String): SortCriteria = entries.find { it.name == key } ?: RECENT
    }
}
