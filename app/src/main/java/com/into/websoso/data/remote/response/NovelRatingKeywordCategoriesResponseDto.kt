package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelRatingKeywordCategoriesResponseDto(
    @SerialName("categories")
    val categories: List<NovelRatingKeywordCategoryResponseDto>,
)
