package com.into.websoso.feature.library.model

enum class SortTypeUiModel(
    val displayName: String,
) {
    RECENT("최신 순"),
    OLD("오래된 순"),
    ;

    companion object {
        fun valueOf(sortType: String): SortTypeUiModel = entries.firstOrNull { it.name == sortType } ?: RECENT
    }
}
