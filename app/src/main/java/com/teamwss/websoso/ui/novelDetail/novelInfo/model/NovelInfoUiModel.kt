package com.teamwss.websoso.ui.novelDetail.novelInfo.model

data class NovelInfoUiModel(
    val novelDescription: String,
    val attractivePoints: List<String>,
    val watchingCount: Int,
    val watchedCount: Int,
    val quitCount: Int,
)

data class PlatformModel(
    val platformName: String,
    val platformImage: String,
    val platformUrl: String,
)

data class KeywordModel(
    val keywordName: String,
    val keywordCount: Int,
)

data class ExpandTextUiModel(
    var expandTextToggleVisibility: Boolean,
    var isExpandTextToggleSelected: Boolean,
    var bodyMaxLines: Int,
) {
    companion object {
        const val DEFAULT_BODY_MAX_LINES: Int = 3
    }
}
