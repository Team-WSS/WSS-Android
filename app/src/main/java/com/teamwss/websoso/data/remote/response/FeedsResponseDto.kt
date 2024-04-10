package com.teamwss.websoso.data.remote.response

data class FeedsResponseDto(
    val category: String,
    val feedsResponseDto: List<FeedResponseDto>
)