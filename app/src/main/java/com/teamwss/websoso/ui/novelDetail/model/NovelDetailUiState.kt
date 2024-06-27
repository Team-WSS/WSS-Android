package com.teamwss.websoso.ui.novelDetail.model

sealed interface NovelDetailUiState {
    data object Loading : NovelDetailUiState
    data object Error : NovelDetailUiState
    data class Success(val novelDetail: NovelDetailModel) : NovelDetailUiState
}