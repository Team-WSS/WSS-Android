package com.teamwss.websoso.data.remote.response

import com.teamwss.websoso.data.model.GenrePreferenceEntity
import com.teamwss.websoso.ui.main.myPage.myActivity.model.Genres
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class GenrePreferenceResponseDto(
    @SerialName("genrePreferences")
    val genrePreferences: List<GenrePreferenceDto>,
)
@Serializable
data class GenrePreferenceDto(
    @SerialName("genreName")
    val genreName: String,
    @SerialName("genreImage")
    val genreImage: String,
    @SerialName("genreCount")
    val genreCount: Int,
){
    fun toData(): GenrePreferenceEntity {
        val koreanGenreName = Genres.from(genreName)?.korean ?: genreName
        return GenrePreferenceEntity(
            genreName = koreanGenreName,
            genreImage = genreImage,
            genreCount = genreCount,
        )
    }
}