package com.into.websoso.data.remote.request

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
    @SerialName("images")
    val images: List<String>?,
)
