package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserNovelStatsResponseDto(
    @SerialName("interestNovelCount")
    val interestNovelCount: Int,
    @SerialName("watchingNovelCount")
    val watchingNovelCount: Int,
    @SerialName("watchedNovelCount")
    val watchedNovelCount: Int,
    @SerialName("quitNovelCount")
    val quitNovelCount: Int,
)
