package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserStorageResponseDto(
    @SerialName("userNovelCount")
    val userNovelCount: Long,
    @SerialName("userNovelRating")
    val userNovelRating: Float,
    @SerialName("isLoadable")
    val isLoadable: Boolean,
    @SerialName("userNovels")
    val userNovels: List<StorageNovelDto>,
) {
    @Serializable
    data class StorageNovelDto(
        @SerialName("userNovelId")
        val userNovelId: Long,
        @SerialName("novelId")
        val novelId: Long,
        @SerialName("author")
        val author: String,
        @SerialName("novelImage")
        val novelImage: String,
        @SerialName("title")
        val title: String,
        @SerialName("novelRating")
        val novelRating: Float,
    )
}