package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeywordsResponseDto(
    @SerialName("categories")
    val categories: List<CategoryResponseDto>,
) {
    @Serializable
    data class CategoryResponseDto(
        @SerialName("categoryName")
        val categoryName: String,
        @SerialName("categoryImage")
        val categoryImage: String,
        @SerialName("keywords")
        val keywords: List<KeywordResponseDto>,
    ) {
        @Serializable
        data class KeywordResponseDto(
            @SerialName("keywordId")
            val keywordId: Int,
            @SerialName("keywordName")
            val keywordName: String,
        )
    }
}