package com.teamwss.websoso.ui.novelDetail.model

data class NovelDetailUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val novelDetail: NovelDetailModel = NovelDetailModel(
        NovelDetailModel.UserNovelModel(
            userNovelId = null,
            readStatus = null,
            startDate = null,
            endDate = null,
            isUserNovelInterest = false,
            userNovelRating = 0.0f,
            hasUserNovelInfo = false
        ),
        NovelDetailModel.NovelModel(
            novelTitle = "",
            novelImage = "",
            novelGenres = listOf(),
            formattedNovelGenres = "",
            novelGenreImage = "",
            isNovelCompleted = false,
            author = ""
        ),
        NovelDetailModel.UserRatingModel(
            interestCount = 0,
            novelRating = 0.0f,
            novelRatingCount = 0,
            formattedNovelRating = "",
            feedCount = 0
        ),
    ),
)
