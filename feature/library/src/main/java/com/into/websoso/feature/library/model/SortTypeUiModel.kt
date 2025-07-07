package com.into.websoso.feature.library.model

import com.into.websoso.domain.library.model.SortType

enum class SortTypeUiModel(
    val sortType: SortType,
    val key: String,
    val displayName: String,
) {
    NEWEST(SortType.RECENT, "NEWEST", "최신 순"),
    OLDEST(SortType.OLD, "OLDEST", "오래된 순"),
    ;

    companion object {
        fun from(sortType: SortType): SortTypeUiModel = entries.firstOrNull { it.sortType == sortType } ?: NEWEST
    }
}
