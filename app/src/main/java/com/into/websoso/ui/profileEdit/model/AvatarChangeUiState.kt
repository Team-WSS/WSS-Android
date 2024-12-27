package com.into.websoso.ui.profileEdit.model

data class AvatarChangeUiState(
    val avatars: List<AvatarModel> = emptyList(),
    val selectedAvatar: AvatarModel = AvatarModel(),
    val loading: Boolean = true,
    val error: Boolean = false,
)
