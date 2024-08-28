package com.teamwss.websoso.ui.main.myPage.myActivity.model

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
    val relevantCategories:String,
){
    val formattedScore: String
        get() = String.format(
            "%s (%,d)",
            novelRating?.takeIf { it != null } ?: 0.0f,
            novelRatingCount?.takeIf { it != null } ?: 0
        )

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
            return date?.let { outputFormat.format(it) } ?: "Invalid Date"
        }
    }
}