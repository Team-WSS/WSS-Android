package com.teamwss.websoso.ui.novelDetail.model

sealed class NovelDetailUiState {
    data object Loading : NovelDetailUiState()
    data object Error : NovelDetailUiState()
    data class Success(val novelDetail: NovelDetailModel) : NovelDetailUiState()
}