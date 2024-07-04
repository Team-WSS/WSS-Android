package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelRatingCategoryResponseDto(
    @SerialName("categoryName")
    val categoryName: String,
    @SerialName("keywords")
    val keywords: List<NovelRatingKeywordResponseDto>,
) {
}