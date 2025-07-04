package com.into.websoso.feature.library.model

data class LibraryFilterUiState(
    val isInterested: Boolean = false,
    val readStatusLabel: List<String> = emptyList(),
    val readStatusSelected: Boolean = false,
    val ratingLabel: List<String> = emptyList(),
    val ratingSelected: Boolean = false,
    val attractivePointLabel: List<String> = emptyList(),
    val attractivePointSelected: Boolean = false,
)
