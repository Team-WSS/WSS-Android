package com.teamwss.websoso.ui.novelRating.model

data class NovelRatingUiState(
    val novelRatingModel: NovelRatingModel = NovelRatingModel(0, "", 0f, "", null, null),
    val keywordModel: KeywordModel = KeywordModel(emptyList()),
    val loading: Boolean = true,
    val error: Boolean = false,
)
