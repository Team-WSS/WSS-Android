package com.into.websoso.feature.library.model

import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.NovelRating
import com.into.websoso.domain.library.model.ReadStatuses
import com.into.websoso.domain.library.model.SortCriteria

data class LibraryUiState(
    val isGrid: Boolean = true,
    val libraryFilterUiModel: LibraryFilterUiModel = LibraryFilterUiModel(),
)

data class LibraryFilterUiModel(
    val isInterested: Boolean = false,
    val sortCriteria: SortCriteria = SortCriteria.RECENT,
    val readStatuses: ReadStatuses = ReadStatuses(),
    val attractivePoints: AttractivePoints = AttractivePoints(),
    val novelRating: NovelRating = NovelRating(),
) {
    val ratingText: String
        get() = if (novelRating.isSelected) "${novelRating.rating.value}이상" else "별점"

    val readStatusLabelText: String
        get() = createLabel(
            values = readStatuses.selectedLabels,
            labelTitle = "읽기 상태",
        )

    val attractivePointLabelText: String
        get() = createLabel(
            values = attractivePoints.selectedLabels,
            labelTitle = "매력 포인트",
        )

    val isFilterApplied: Boolean
        get() = readStatuses.isSelected || attractivePoints.isSelected || novelRating.isSelected || isInterested

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
