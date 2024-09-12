package com.teamwss.websoso.ui.novelInfo.model

import com.teamwss.websoso.data.model.NovelInfoEntity
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.novelRating.model.CharmPoint
import com.teamwss.websoso.ui.novelRating.model.ReadStatus

data class NovelInfoUiModel(
    val novelDescription: String = "",
    val attractivePoints: List<CharmPoint> = emptyList(),
    val unifiedReviewCount: UnifiedReviewCountModel = UnifiedReviewCountModel(),
    val isAttractivePointsExist: Boolean = attractivePoints.isNotEmpty(),
    val isUserReviewExist: Boolean = (unifiedReviewCount.watchingCount.count + unifiedReviewCount.watchedCount.count + unifiedReviewCount.quitCount.count) > 0,
) {

    fun formatAttractivePoints(): String {
        return attractivePoints.joinToString(", ") { it.title }
    }
}

data class PlatformsModel(
    val naverModel: PlatformModel? = null,
    val kakaoModel: PlatformModel? = null,
) {

    companion object {
        fun formatPlatforms(platforms: List<NovelInfoEntity.PlatformEntity>): PlatformsModel =
            PlatformsModel(
                naverModel = platforms.find { it.platformName == "네이버시리즈" }?.toUi(),
                kakaoModel = platforms.find { it.platformName == "카카오페이지" }?.toUi(),
            )
    }
}

data class PlatformModel(
    val platformName: String,
    val platformImage: String,
    val platformUrl: String,
    val isVisible: Boolean = platformName.isNotBlank(),
)

data class KeywordModel(
    val keywordName: String,
    val keywordCount: Int,
)

data class UnifiedReviewCountModel(
    val watchingCount: ReviewCountModel = ReviewCountModel(ReadStatus.WATCHING, 0),
    val watchedCount: ReviewCountModel = ReviewCountModel(ReadStatus.WATCHED, 0),
    val quitCount: ReviewCountModel = ReviewCountModel(ReadStatus.QUIT, 0),
) {
    fun formattedUnifiedReviewCount(viewHeight: Int): UnifiedReviewCountModel {
        val watchingGraphHeight = formattedHeight(viewHeight, watchingCount.count)
        val watchedGraphHeight = formattedHeight(viewHeight, watchedCount.count)
        val quitGraphHeight = formattedHeight(viewHeight, quitCount.count)

        return UnifiedReviewCountModel(
            watchingCount = watchingCount.copy(graphHeight = watchingGraphHeight),
            watchedCount = watchedCount.copy(graphHeight = watchedGraphHeight),
            quitCount = quitCount.copy(graphHeight = quitGraphHeight),
        )
    }

    private fun formattedHeight(viewHeight: Int, count: Int): Int {
        val maxCount = maxOf(watchingCount.count, watchedCount.count, quitCount.count)
        if (maxCount == 0) return 0
        return viewHeight * count / maxCount
    }

    fun maxCountReadStatus(): ReadStatus {
        val maxCount = maxOf(watchingCount.count, watchedCount.count, quitCount.count)
        return when (maxCount) {
            watchingCount.count -> ReadStatus.WATCHING
            watchedCount.count -> ReadStatus.WATCHED
            quitCount.count -> ReadStatus.QUIT
            else -> ReadStatus.WATCHING
        }
    }
}

data class ReviewCountModel(
    val readStatus: ReadStatus,
    val count: Int,
    val isVisible: Boolean = count > 0,
    val graphHeight: Int = 0,
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
