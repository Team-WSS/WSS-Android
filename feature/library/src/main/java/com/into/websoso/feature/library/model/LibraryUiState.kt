package com.into.websoso.feature.library.model

data class LibraryUiState(
    val novels: List<LibraryListItemModel> = emptyList(),
    val isGrid: Boolean = true,
    val selectedSortType: SortTypeUiModel = SortTypeUiModel.NEWEST,
    val filterUiState: LibraryFilterUiState = LibraryFilterUiState(),
)
