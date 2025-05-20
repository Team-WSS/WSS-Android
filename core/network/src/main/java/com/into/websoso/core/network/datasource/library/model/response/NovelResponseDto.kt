package com.into.websoso.core.network.datasource.library.model.response

import com.into.websoso.data.library.model.NovelEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class NovelResponseDto(
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
) {
    internal fun toData(): NovelEntity =
        NovelEntity(
            userNovelId = userNovelId,
            novelId = novelId,
            author = author,
            novelImage = novelImage,
            title = title,
            novelRating = novelRating,
        )
}
