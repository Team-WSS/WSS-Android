package com.teamwss.websoso.ui.feed.model

data class FeedUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val categories: List<CategoryUiState>,
    val isLoadable: Boolean = true,
    val feeds: List<FeedModel> = emptyList(),
) {

    data class CategoryUiState(
        val category: Category = Category.ALL,
        val isSelected: Boolean = true,
    )
}
