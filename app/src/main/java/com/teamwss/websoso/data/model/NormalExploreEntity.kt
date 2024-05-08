package com.teamwss.websoso.data.model

data class NormalExploreEntity(
    val resultCount: Long,
    val isLoadable: Boolean,
    val novels: List<Novel>,
) {
    data class Novel(
        val id: Long,
        val title: String,
        val author: String,
        val image: String,
        val interestedCount: Long,
        val rating: Float,
        val ratingCount: Long,
    )
}