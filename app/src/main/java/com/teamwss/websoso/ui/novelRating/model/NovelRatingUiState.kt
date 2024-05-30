package com.teamwss.websoso.ui.novelRating.model

data class NovelRatingUiState(
    val novelRatingModel: NovelRatingModel,
    val loading: Boolean = true,
    val error: Boolean = false,
)

data class NovelRatingModel(
    val userNovelId: Long,
    val novelTitle: String,
    val userNovelRating: Float,
    val readStatus: String,
    val startDate: String?,
    val endDate: String?,
    var uiReadStatus: ReadStatus = ReadStatus.valueOf(readStatus),
    var currentStartDate: Triple<Int, Int, Int>? = startDate.toFormattedDate(),
    var currentEndDate: Triple<Int, Int, Int>? = endDate.toFormattedDate(),
) {
    companion object {
        fun String?.toFormattedDate(): Triple<Int, Int, Int>? {
            val date = this?.split("-") ?: return null
            return Triple(date[0].toInt(), date[1].toInt(), date[2].toInt())
        }
    }
}