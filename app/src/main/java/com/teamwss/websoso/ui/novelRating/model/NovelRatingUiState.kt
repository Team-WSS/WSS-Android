package com.teamwss.websoso.ui.novelRating.model

data class NovelRatingUiState(
    val novelRatingModel: NovelRatingModel =
        NovelRatingModel(
            novelTitle = "",
            readStatus = ReadStatus.WATCHING.toString(),
            startDate = null,
            endDate = null,
            userNovelRating = 0.0f,
            attractivePoints = emptyList(),
            userKeywords = emptyList(),
        ),
    val keywords: NovelRatingKeywordsModel = NovelRatingKeywordsModel(emptyList()),
    val maxDayValue: Int = 0,
    val isEditingStartDate: Boolean = true,
    val loading: Boolean = true,
    val error: Boolean = false,
)
