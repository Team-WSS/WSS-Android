package com.teamwss.websoso.domain.model

data class NormalExploreResult(
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