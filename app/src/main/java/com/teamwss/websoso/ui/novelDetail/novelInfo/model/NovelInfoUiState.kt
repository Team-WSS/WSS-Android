package com.teamwss.websoso.ui.novelDetail.novelInfo.model

data class NovelInfoUiState(
    val novelInfoModel: NovelInfoUiModel = NovelInfoUiModel(
        "",
        emptyList(),
        UnifiedReviewCountModel(
            ReviewCountModel(ReadStatus.WATCHING, 0),
            ReviewCountModel(ReadStatus.WATCHED, 0),
            ReviewCountModel(ReadStatus.QUIT, 0),
        ),
    ),
    val keywords: List<KeywordModel> = emptyList(),
    val platforms: PlatformsModel = PlatformsModel(null, null),
    val expandTextModel: ExpandTextUiModel = ExpandTextUiModel(),
    val loading: Boolean = true,
    val error: Boolean = false,
)
