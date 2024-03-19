package com.teamwss.websoso.data.model

data class FeedEntity(
    val id: Int,
    val user: UserInfo,
    val content: String,
    val genre: List<String>,
    val novelInfo: NovelInfo,
    val likeCount: Int,
    val commentCount: Int,
) {
    data class UserInfo(
        val id: Int,
        val name: String,
        val profileImage: String,
    )

    data class NovelInfo(
        val id: Int,
        val name: String,
        val score: Double,
        val count: Int,
    )
}
