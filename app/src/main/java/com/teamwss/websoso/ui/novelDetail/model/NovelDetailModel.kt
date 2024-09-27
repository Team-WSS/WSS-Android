package com.teamwss.websoso.ui.novelDetail.model

import com.teamwss.websoso.ui.novelRating.model.ReadStatus
import java.text.SimpleDateFormat
import java.util.Locale

data class NovelDetailModel(
    val userNovel: UserNovelModel = UserNovelModel(),
    val novel: NovelModel = NovelModel(),
    val userRating: UserRatingModel = UserRatingModel(),
    val isFirstLaunched: Boolean = false,
    val isLogin: Boolean = false,
) {
    data class UserNovelModel(
        val userNovelId: Long? = null,
        val readStatus: ReadStatus? = null,
        val startDate: String? = null,
        val endDate: String? = null,
        val isUserNovelInterest: Boolean = false,
        val userNovelRating: Float = 0.0f,
        val isAlreadyDateRated: Boolean = startDate != null || endDate != null,
        val isAlreadyRated: Boolean = userNovelId != null && isUserNovelInterest,
        val formattedUserNovelDate: String = formattedDateRange(startDate, endDate),
    )

    data class NovelModel(
        val novelId: Long = 0,
        val novelTitle: String = "",
        val novelImage: String = "",
        val novelGenres: String = "",
        val novelGenreImage: String = "",
        val isNovelCompleted: Boolean = false,
        val isNovelCompletedText: String = if (isNovelCompleted) "완결작" else "연재중",
        val author: String = "",
        val formattedNovelDetailSummary: String = "$novelGenres ・ $isNovelCompletedText ・ $author",
        val isNovelNotBlank: Boolean = novelTitle.isNotBlank() && novelImage.isNotBlank() && author.isNotBlank(),
    )

    data class UserRatingModel(
        val interestCount: Int = 0,
        val novelRating: Float = 0.0f,
        val novelRatingCount: Int = 0,
        val formattedNovelRating: String = "$novelRating ($novelRatingCount)",
        val feedCount: Int = 0,
    )

    companion object {
        fun formattedDateRange(startDate: String?, endDate: String?): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yy. MM. dd", Locale.getDefault())
            val start = startDate?.let { inputFormat.parse(it) }
            val end = endDate?.let { inputFormat.parse(it) }

            return when {
                start != null && end != null -> "${outputFormat.format(start)} ~ ${
                    outputFormat.format(
                        end
                    )
                }"

                start != null -> outputFormat.format(start)
                end != null -> outputFormat.format(end)
                else -> ""
            }
        }
    }
}
