package com.teamwss.websoso.ui.novelDetail.novelInfo.model

data class NovelInfoUiState(
    val novelInfoModel: NovelInfoUiModel = NovelInfoUiModel(
        "",
        emptyList(),
        0,
        0,
        0,
    ),
    val keywords: List<KeywordModel> = emptyList(),
    val platforms: List<PlatformModel> = emptyList(),
    val expandTextModel: ExpandTextUiModel = ExpandTextUiModel(
        expandTextToggleVisibility = false,
        isExpandTextToggleSelected = false,
        bodyMaxLines = ExpandTextUiModel.DEFAULT_BODY_MAX_LINES,
    ),
    val loading: Boolean = true,
    val error: Boolean = false,
)
