package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenrePreferenceResponseDto(
    @SerialName("genrePreferences")
    val genrePreferences: List<GenrePreferenceDto>,
) {
    @Serializable
    data class GenrePreferenceDto(
        @SerialName("genreName")
        val genreName: String,
        @SerialName("genreImage")
        val genreImage: String,
        @SerialName("genreCount")
        val genreCount: Int,
    )
}