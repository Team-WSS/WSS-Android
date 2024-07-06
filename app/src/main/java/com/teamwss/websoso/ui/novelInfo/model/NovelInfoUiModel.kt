package com.teamwss.websoso.ui.novelInfo.model

import com.teamwss.websoso.data.model.NovelInfoEntity
import com.teamwss.websoso.ui.mapper.toUi

data class NovelInfoUiModel(
    val novelDescription: String,
    val attractivePoints: List<String>,
    val unifiedReviewCount: UnifiedReviewCountModel,
)

data class PlatformsModel(
    val naverModel: PlatformModel? = null,
    val kakaoModel: PlatformModel? = null,
) {
    fun formatPlatforms(platforms: List<NovelInfoEntity.PlatformEntity>): PlatformsModel =
        PlatformsModel(
            naverModel = platforms.find { it.platformName == "네이버시리즈" }?.toUi(),
            kakaoModel = platforms.find { it.platformName == "카카오페이지" }?.toUi(),
        )
}

data class PlatformModel(
    val platformName: String,
    val platformImage: String,
    val platformUrl: String,
    val isVisible: Boolean,
)

data class KeywordModel(
    val keywordName: String,
    val keywordCount: Int,
)

data class UnifiedReviewCountModel(
    val watchingCount: ReviewCountModel,
    val watchedCount: ReviewCountModel,
    val quitCount: ReviewCountModel,
)

data class ReviewCountModel(
    val readStatus: ReadStatus,
    val count: Int,
    val isVisible: Boolean = count > 0,
)

data class ExpandTextUiModel(
    val expandTextToggleVisibility: Boolean = false,
    val isExpandTextToggleSelected: Boolean = false,
    val bodyMaxLines: Int = DEFAULT_BODY_MAX_LINES,
) {
    companion object {
        const val DEFAULT_BODY_MAX_LINES: Int = 3
    }
}
