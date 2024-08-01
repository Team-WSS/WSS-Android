package com.teamwss.websoso.ui.novelDetail.model

import com.teamwss.websoso.ui.novelRating.model.ReadStatus
import java.text.SimpleDateFormat
import java.util.Locale

data class NovelDetailModel(
    val userNovel: UserNovelModel,
    val novel: NovelModel,
    val userRating: UserRatingModel,
) {
    data class UserNovelModel(
        val userNovelId: Long?,
        val readStatus: ReadStatus?,
        val startDate: String?,
        val endDate: String?,
        val isUserNovelInterest: Boolean,
        val userNovelRating: Float,
        val isAlreadyRated: Boolean = userNovelId != null,
        val formattedUserNovelDate: String = formattedDateRange(startDate, endDate),
    )

    data class NovelModel(
        val novelId: Long,
        val novelTitle: String,
        val novelImage: String,
        val novelGenres: List<Category>,
        val formattedNovelGenres: String = novelGenres.joinToString(separator = "/") { it.titleKr },
        val novelGenreImage: String,
        val isNovelCompleted: Boolean,
        val isNovelCompletedText: String = if (isNovelCompleted) "완결작" else "연재중",
        val author: String,
        val formattedNovelDetailSummary: String = "$formattedNovelGenres ・ $isNovelCompletedText ・ $author",
    )

    data class UserRatingModel(
        val interestCount: Int,
        val novelRating: Float,
        val novelRatingCount: Int,
        val formattedNovelRating: String = "$novelRating ($novelRatingCount)",
        val feedCount: Int,
    )

    companion object {
        fun formattedDateRange(startDate: String?, endDate: String?): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yy. MM. dd", Locale.getDefault())
            val start = startDate?.let { inputFormat.parse(it) }
            val end = endDate?.let { inputFormat.parse(it) }

            return when {
                start != null && end != null -> "${outputFormat.format(start)} ~ ${outputFormat.format(end)}"
                start != null -> outputFormat.format(start)
                end != null -> outputFormat.format(end)
                else -> ""
            }
        }
    }

    val defaultUserNovelModel = UserNovelModel(
        userNovelId = null,
        readStatus = null,
        startDate = null,
        endDate = null,
        isUserNovelInterest = false,
        userNovelRating = 0.0f,
        isAlreadyRated = false,
        formattedUserNovelDate = "",
    )
}
