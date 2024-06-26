package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelRatingKeywordResponseDto(
    @SerialName("categories")
    val categories: List<CategoryResponseDto>,
) {
    @Serializable
    data class CategoryResponseDto(
        @SerialName("categoryName")
        val categoryName: String,
        @SerialName("keywords")
        val keywords: List<KeywordResponseDto>,
    ) {
        @Serializable
        data class KeywordResponseDto(
            @SerialName("keywordId")
            val keywordId: Long,
            @SerialName("keywordName")
            val keywordName: String,
        )
    }
}
