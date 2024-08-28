package com.teamwss.websoso.ui.normalExplore.model

data class NormalExploreModel(
    val resultCount: Long,
    val isLoadable: Boolean,
    val novelModels: List<NovelModel>,
) {
    data class NovelModel(
        val id: Long,
        val title: String,
        val author: String,
        val image: String,
        val interestedCount: Long,
        val rating: Float,
        val ratingCount: Long,
    )
}