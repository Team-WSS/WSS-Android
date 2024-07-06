package com.teamwss.websoso.ui.novelRating.model

data class NovelRatingUiState(
    val novelRatingModel: NovelRatingModel =
        NovelRatingModel(
            novelTitle = "",
            readStatus = ReadStatus.WATCHING.toString(),
            startDate = null,
            endDate = null,
            userNovelRating = 0.0f,
            charmPoints = emptyList(),
            userKeywords = emptyList(),
        ),
    val keywordsModel: NovelRatingKeywordsModel = NovelRatingKeywordsModel(emptyList()),
    val maxDayValue: Int = 0,
    val isEditingStartDate: Boolean = true,
    val loading: Boolean = true,
    val error: Boolean = false,
)
