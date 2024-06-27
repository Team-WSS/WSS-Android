package com.teamwss.websoso.ui.novelDetail.model

data class NovelDetailUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val novelDetail: NovelDetailModel =
        NovelDetailModel(
            NovelDetailModel.UserNovelModel(0, "", null, null, false, 0f),
            NovelDetailModel.NovelModel("", "", emptyList(), "", false, ""),
            NovelDetailModel.UserRatingModel(0, 0f, 0, 0),
        ),
)
