package com.into.websoso.feature.library.model

data class LibraryUiState(
    val isGrid: Boolean = true,
    val novelTotalCount: Long = 0,
    val libraryFilterUiModel: LibraryFilterUiModel = LibraryFilterUiModel(),
)
