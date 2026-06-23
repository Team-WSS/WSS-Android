package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PopularNovelsResponseDto(
    @SerialName("popularNovels")
    val popularNovels: List<PopularNovelResponseDto>,
) {
    @Serializable
    data class PopularNovelResponseDto(
        @SerialName("author")
        val author: String = "",
        @SerialName("avatarImage")
        val avatarImage: String? = null,
        @SerialName("feedContent")
        val feedContent: String? = null,
        @SerialName("genreName")
        val genreName: String = "",
        @SerialName("isNovelCompleted")
        val isNovelCompleted: Boolean = false,
        @SerialName("keywords")
        val keywords: List<String> = emptyList(),
        @SerialName("nickname")
        val nickname: String? = null,
        @SerialName("novelDescription")
        val novelDescription: String = "",
        @SerialName("novelId")
        val novelId: Long,
        @SerialName("novelImage")
        val novelImage: String,
        @SerialName("title")
        val title: String,
    )
}
