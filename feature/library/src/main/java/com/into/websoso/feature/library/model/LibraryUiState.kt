package com.into.websoso.feature.library.model

data class LibraryUiState(
    val isGrid: Boolean = true,
    val libraryFilterUiModel: LibraryFilterUiModel = LibraryFilterUiModel(),
)

