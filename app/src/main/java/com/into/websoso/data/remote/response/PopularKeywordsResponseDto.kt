package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PopularKeywordsResponseDto(
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
