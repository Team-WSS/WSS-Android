package com.into.websoso.feature.library.model

internal data class LibraryFilterUiState(
    val isInterested: Boolean = false,
    val readStatusLabel: List<String> = emptyList(),
    val readStatusSelected: Boolean = false,
    val ratingLabel: List<String> = emptyList(),
    val ratingSelected: Boolean = false,
    val attractivePointLabel: List<String> = emptyList(),
    val attractivePointSelected: Boolean = false,
) {
    val readStatusLabelText: String = buildLabel(readStatusLabel, "읽기 상태")
    val attractivePointLabelText: String = buildLabel(attractivePointLabel, "매력 포인트")

    private fun buildLabel(
        values: List<String>,
        labelTitle: String,
    ): String =
        when {
            values.isEmpty() -> labelTitle
            values.size == 1 -> values.first()
            else -> "${values.first()} 외 ${values.size - 1}개"
        }
}
