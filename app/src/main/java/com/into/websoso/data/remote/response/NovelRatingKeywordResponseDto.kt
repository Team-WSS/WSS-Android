package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelRatingKeywordResponseDto(
    @SerialName("keywordId")
    val keywordId: Int,
    @SerialName("keywordName")
    val keywordName: String,
)
