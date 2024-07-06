package com.teamwss.websoso.ui.novelInfo.model

data class NovelInfoUiState(
    val novelInfoModel: NovelInfoUiModel = NovelInfoUiModel(),
    val keywords: List<KeywordModel> = emptyList(),
    val platforms: PlatformsModel = PlatformsModel(),
    val expandTextModel: ExpandTextUiModel = ExpandTextUiModel(),
    val loading: Boolean = true,
    val error: Boolean = false,
)
