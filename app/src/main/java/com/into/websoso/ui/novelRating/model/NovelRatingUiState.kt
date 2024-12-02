package com.into.websoso.ui.novelRating.model

data class NovelRatingUiState(
    val novelRatingModel: NovelRatingModel = NovelRatingModel(),
    val keywordsModel: NovelRatingKeywordsModel = NovelRatingKeywordsModel(),
    val maxDayValue: Int = 0,
    val isEditingStartDate: Boolean = true,
    val isAlreadyRated: Boolean = false,
    val loading: Boolean = true,
    val isFetchError: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val isSaveError: Boolean = false,
)
