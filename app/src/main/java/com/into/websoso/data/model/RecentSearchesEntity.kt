package com.into.websoso.data.model

data class RecentSearchesEntity(
    val recentSearches: List<RecentSearchEntity>,
) {
    data class RecentSearchEntity(
        val id: Long,
        val keyword: String,
    )
}

