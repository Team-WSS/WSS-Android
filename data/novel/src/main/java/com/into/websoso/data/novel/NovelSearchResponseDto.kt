package com.into.websoso.data.novel

import com.into.websoso.data.novel.model.NovelSearchEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NovelSearchResponseDto(
    @SerialName("isLoadable")
    val isLoadable: Boolean,
    @SerialName("novels")
    val novels: List<NovelDto>,
) {
    @Serializable
    data class NovelDto(
        @SerialName("novelId")
        val novelId: Long,
        @SerialName("title")
        val title: String,
        @SerialName("author")
        val author: String,
        @SerialName("novelImage")
        val imageUrl: String,
    ) {
        fun toData(): NovelSearchEntity =
            NovelSearchEntity(
                novelId = novelId,
                title = title,
                author = author,
                imageUrl = imageUrl,
            )
    }
}
