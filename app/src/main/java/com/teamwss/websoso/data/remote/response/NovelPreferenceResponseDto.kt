package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelPreferenceResponseDto(
    @SerialName("attractivePoints")
    val attractivePoints: List<String>,
    @SerialName("keywords")
    val keywords: List<AttractivePointKeywordDto>,
)

@Serializable
data class AttractivePointKeywordDto(
    @SerialName("keywordName")
    val keywordName: String,
    @SerialName("keywordCount")
    val keywordCount: Int,
)