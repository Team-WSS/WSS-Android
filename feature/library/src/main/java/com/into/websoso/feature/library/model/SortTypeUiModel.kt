package com.into.websoso.feature.library.model

import com.into.websoso.domain.library.model.SortType

enum class SortTypeUiModel(
    val sortType: SortType,
    val key: String,
    val displayName: String,
) {
    NEWEST(SortType.NEWEST, "NEWEST", "최신 순"),
    OLDEST(SortType.OLDEST, "OLDEST", "오래된 순");

    companion object {
        fun from(sortType: SortType): SortTypeUiModel {
            return entries.firstOrNull { it.sortType == sortType } ?: NEWEST
        }
    }
}
