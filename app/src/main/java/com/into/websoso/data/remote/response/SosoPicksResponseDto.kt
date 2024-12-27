package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SosoPicksResponseDto(
    @SerialName("sosoPicks")
    val sosoPicks: List<SosoPickResponseDto>,
) {
    @Serializable
    data class SosoPickResponseDto(
        @SerialName("novelId")
        val novelId: Long,
        @SerialName("novelImage")
        val novelImage: String,
        @SerialName("title")
        val title: String,
    )
}