package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecentSearchesResponseDto(
    @SerialName("recentSearches")
    val recentSearches: List<RecentSearchResponseDto>,
) {
    @Serializable
    data class RecentSearchResponseDto(
        @SerialName("id")
        val id: Long,
        @SerialName("keyword")
        val keyword: String,
    )
}

