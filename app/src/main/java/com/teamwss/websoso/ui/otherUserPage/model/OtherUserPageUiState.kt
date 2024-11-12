package com.teamwss.websoso.ui.otherUserPage.model

import com.teamwss.websoso.data.model.OtherUserProfileEntity

data class OtherUserPageUiState(
    val otherUserProfile: OtherUserProfileEntity? = null,
    val userId: Long = 0L,
    val isBlockedCompleted: Boolean = false,
    val isLoading: Boolean = false,
    val error: Boolean = false,
)