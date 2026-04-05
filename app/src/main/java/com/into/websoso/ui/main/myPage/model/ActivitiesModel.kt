package com.into.websoso.ui.main.myPage.model

data class ActivitiesModel(
    val isLoadable: Boolean,
    val activities: List<ActivityModel>,
) {
    data class ActivityModel(
        val feedId: Long,
        val isSpoiler: Boolean,
        val feedContent: String,
        val createdDate: String,
        val isModified: Boolean,
        val isLiked: Boolean,
        val isPublic: Boolean,
        val likeCount: Int,
        val commentCount: Int,
        val novelId: Long?,
        val title: String?,
        val novelRatingCount: Int?,
        val novelRating: Float?,
    ) {
        val isNovelInfoAvailable: Boolean
            get() = novelId != null

        val formattedScore: String
            get() = String.format(
                SCORE_FORMAT,
                novelRating?.let { String.format("%.1f", it) },
                novelRatingCount?.takeIf { it != null },
            )

        companion object {
            private const val SCORE_FORMAT = "%s (%,d)"
        }
    }
}
