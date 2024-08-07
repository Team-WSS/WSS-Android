package com.teamwss.websoso.ui.feedDetail.model

interface FeedDetailUiState {
    data class Success(
        val feedDetail: FeedDetailModel,
    ) : FeedDetailUiState

    data object Loading : FeedDetailUiState
    data object Error : FeedDetailUiState
}