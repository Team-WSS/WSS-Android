package com.teamwss.websoso.ui.novelRating.model

data class NovelRatingUiState(
    val novelRatingModel: NovelRatingModel = NovelRatingModel(),
    val keywordsModel: NovelRatingKeywordsModel = NovelRatingKeywordsModel(emptyList()),
    val maxDayValue: Int = 0,
    val isAlreadyRated: Boolean = false,
    val isEditingStartDate: Boolean = true,
    val loading: Boolean = true,
    val error: Boolean = false,
)
