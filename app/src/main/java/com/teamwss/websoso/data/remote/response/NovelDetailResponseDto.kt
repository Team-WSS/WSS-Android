package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelDetailResponseDto(
    @SerialName("userNovelId")
    val userNovelId: Long,
    @SerialName("novelId")
    val novelTitle: String,
    @SerialName("novelImage")
    val novelImage: String,
    @SerialName("novelGenres")
    val novelGenres: List<String>,
    @SerialName("novelGenreImage")
    val novelGenreImage: String,
    @SerialName("isNovelCompleted")
    val isNovelCompleted: Boolean,
    @SerialName("author")
    val author: String,
    @SerialName("interestCount")
    val interestCount: Int,
    @SerialName("novelRating")
    val novelRating: Float,
    @SerialName("novelRatingCount")
    val novelRatingCount: Int,
    @SerialName("feedCount")
    val feedCount: Int,
    @SerialName("userNovelRating")
    val userNovelRating: Float,
    @SerialName("readStatus")
    val readStatus: String,
    @SerialName("startDate")
    val startDate: String?,
    @SerialName("endDate")
    val endDate: String?,
    @SerialName("isUserNovelInterest")
    val isUserNovelInterest: Boolean,
)
