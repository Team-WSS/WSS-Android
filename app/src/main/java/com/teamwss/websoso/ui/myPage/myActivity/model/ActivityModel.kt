package com.teamwss.websoso.ui.myPage.myActivity.model

import java.text.SimpleDateFormat
import java.util.Locale

data class ActivityModel(
    val feedId: Int,
    val userId: Int,
    val profileImg: String,
    val nickname: String,
    val isSpoiler: Boolean,
    val feedContent: String,
    val createdDate: String,
    val isModified: Boolean,
    val isLiked: Boolean,
    val likeCount: Int,
    val commentCount: Int,
    val novelId: Int,
    val title: String,
    val novelRatingCount: Int?,
    val novelRating: Float?,
    val relevantCategories: String
) {
    companion object {
        fun translateGenres(relevantCategories: List<String>): String {
            return relevantCategories.joinToString(", ") { category ->
                Genres.from(category)?.korean ?: category
            }
        }

        fun formatDate(inputDate: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("M월 d일", Locale.getDefault())

            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date!!)
        }
    }
}
