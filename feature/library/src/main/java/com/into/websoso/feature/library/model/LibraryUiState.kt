package com.into.websoso.feature.library.model

import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.ReadStatus

data class LibraryUiState(
    val isGrid: Boolean = true,
    val libraryFilterUiState: LibraryFilterUiState = LibraryFilterUiState(),
)

data class LibraryFilterUiState(
    val selectedSortType: SortTypeUiModel = SortTypeUiModel.RECENT,
    val isInterested: Boolean = false,
    val readStatuses: Map<ReadStatus, Boolean> = mapOf(
        ReadStatus.WATCHING to false,
        ReadStatus.WATCHED to false,
        ReadStatus.QUIT to false,
    ),
    val attractivePoints: Map<AttractivePoints, Boolean> = mapOf(
        AttractivePoints.VIBE to false,
        AttractivePoints.WORLDVIEW to false,
        AttractivePoints.CHARACTER to false,
        AttractivePoints.MATERIAL to false,
        AttractivePoints.RELATIONSHIP to false,
    ),
    val novelRating: Float = 0f,
) {
    val isRatingSelected: Boolean = novelRating != 0f
    val ratingText: String
        get() = if (isRatingSelected) "${novelRating}이상" else "별점"

    val readStatusLabelText: String
        get() = buildLabel(
            readStatuses.filterValues { it }.keys.map { status ->
                ReadStatusUiModel.valueOf(status.name).label
            },
            "읽기 상태",
        )

    val attractivePointLabelText: String
        get() = buildLabel(
            attractivePoints.filterValues { it }.keys.map { point ->
                point.label
            },
            "매력 포인트",
        )

    companion object {
        fun buildLabel(
            values: List<String>,
            labelTitle: String,
        ): String =
            when {
                values.isEmpty() -> labelTitle
                values.size == 1 -> values.first()
                else -> "${values.first()} 외 ${values.size - 1}개"
            }
    }
}
