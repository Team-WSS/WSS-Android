package com.into.websoso.feature.library.model

import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.Genres
import com.into.websoso.domain.library.model.Keywords
import com.into.websoso.domain.library.model.RatingFilter
import com.into.websoso.domain.library.model.ReadStatuses
import com.into.websoso.domain.library.model.SeriesStatuses
import com.into.websoso.domain.library.model.SortCriteria

data class LibraryFilterUiModel(
    val isInterested: Boolean = false,
    val sortCriteria: SortCriteria = SortCriteria.RECENT,
    val readStatuses: ReadStatuses = ReadStatuses(),
    val genres: Genres = Genres(),
    val seriesStatuses: SeriesStatuses = SeriesStatuses(),
    val attractivePoints: AttractivePoints = AttractivePoints(),
    val ratingFilter: RatingFilter = RatingFilter(),
    val keywords: Keywords = Keywords(),
) {
    val ratingText: String
        get() = if (ratingFilter.isSelected) ratingFilter.chipLabel else "별점"

    val readStatusLabelText: String
        get() = createLabel(
            values = readStatuses.selectedLabels,
            labelTitle = "읽기상태",
        )

    val attractivePointLabelText: String
        get() = createLabel(
            values = attractivePoints.selectedLabels,
            labelTitle = "매력포인트",
        )

    val genreLabelText: String
        get() = createLabel(
            values = genres.selectedLabels,
            labelTitle = "장르",
        )

    val seriesStatusLabelText: String
        get() = createLabel(
            values = seriesStatuses.selectedLabels,
            labelTitle = "연재상태",
        )

    val keywordLabelText: String
        get() = createLabel(
            values = keywords.selectedLabels,
            labelTitle = "키워드",
        )

    val isFilterApplied: Boolean
        get() = readStatuses.isSelected ||
            genres.isSelected ||
            seriesStatuses.isSelected ||
            attractivePoints.isSelected ||
            ratingFilter.isSelected ||
            keywords.isSelected ||
            isInterested

    private fun createLabel(
        labelTitle: String,
        values: List<String>,
    ): String =
        when {
            values.isEmpty() -> labelTitle
            values.size == 1 -> values.first()
            else -> "${values.first()} 외 ${values.size - 1}"
        }
}
