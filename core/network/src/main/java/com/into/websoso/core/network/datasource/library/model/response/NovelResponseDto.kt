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
    @SerialName("title")
    val title: String,
    @SerialName("novelImage")
    val novelImage: String,
    @SerialName("novelRating")
    val novelRating: Float,
    @SerialName("readStatus")
    val readStatus: String,
    @SerialName("isInterest")
    val isInterest: Boolean,
    @SerialName("userNovelRating")
    val userNovelRating: Float,
    @SerialName("attractivePoints")
    val attractivePoints: List<String>,
    @SerialName("startDate")
    val startDate: String?,
    @SerialName("endDate")
    val endDate: String?,
    @SerialName("keywords")
    val keywords: List<String>,
    @SerialName("myFeeds")
    val myFeeds: List<String>,
) {
    internal fun toData(): NovelEntity =
        NovelEntity(
            userNovelId = userNovelId,
            novelId = novelId,
            title = title,
            novelImage = novelImage,
            novelRating = novelRating,
            readStatus = readStatus,
            isInterest = isInterest,
            userNovelRating = userNovelRating,
            attractivePoints = attractivePoints,
            startDate = startDate.orEmpty(),
            endDate = endDate.orEmpty(),
            keywords = keywords,
            myFeeds = myFeeds,
        )
}
