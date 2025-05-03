package com.into.websoso.ui.main.myPage.myActivity.model

import java.text.SimpleDateFormat
import java.util.Locale

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
        val relevantCategories: String?,
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
            private const val INPUT_DATE_FORMAT = "yyyy-MM-dd"
            private const val OUTPUT_DATE_FORMAT = "M월 d일"

            fun translateGenres(relevantCategories: List<String>): String {
                return relevantCategories.joinToString(", ") { category ->
                    Genres.from(category)?.korean ?: category
                }
            }

            fun formatDate(inputDate: String): String {
                val inputFormat = SimpleDateFormat(INPUT_DATE_FORMAT, Locale.getDefault())
                val outputFormat = SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.getDefault())

                val date = inputFormat.parse(inputDate)
                return date?.let { outputFormat.format(it) } ?: "Invalid Date"
            }
        }
    }
}
