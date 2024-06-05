package com.teamwss.websoso.data.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelRatingKeywordResponseDto(
    @SerialName("categories")
    val categories: List<Category>,
) {
    @Serializable
    data class Category(
        @SerialName("categoryName")
        val categoryName: String,
        @SerialName("keywords")
        val keywords: List<Keyword>,
    ) {
        @Serializable
        data class Keyword(
            @SerialName("keywordId")
            val keywordId: Long,
            @SerialName("keywordName")
            val keywordName: String,
        )
    }
}