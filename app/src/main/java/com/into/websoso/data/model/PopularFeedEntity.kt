package com.into.websoso.data.model

data class PopularFeedEntity(
    val feedId: Long,
    val feesContent: String,
    val likeCount: Int,
    val commentCount: Int,
    val isSpoiler: Boolean,
    val isPublic: Boolean,
    val novelTitle: String,
    val novelImage: String,
    val novelGenre: String,
)
