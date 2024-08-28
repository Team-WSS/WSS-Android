package com.teamwss.websoso.ui.profileEdit.model

sealed interface AvatarChangeUiState {
    data class Success(val avatars: List<AvatarModel>) : AvatarChangeUiState
    data object Loading : AvatarChangeUiState
    data object Error : AvatarChangeUiState
}
