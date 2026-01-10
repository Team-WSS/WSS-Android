package com.into.websoso.core.network.datasource.feed.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedRequestDto(
    @SerialName("relevantCategories")
    val relevantCategories: List<String>,
    @SerialName("feedContent")
    val feedContent: String,
    @SerialName("novelId")
    val novelId: Long?,
    @SerialName("isSpoiler")
    val isSpoiler: Boolean,
    @SerialName("isPublic")
    val isPublic: Boolean,
)
