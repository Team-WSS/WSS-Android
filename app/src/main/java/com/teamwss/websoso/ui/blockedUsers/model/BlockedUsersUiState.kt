package com.teamwss.websoso.ui.blockedUsers.model

import com.teamwss.websoso.data.model.BlockedUsersEntity.BlockedUserEntity

data class BlockedUsersUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val blockedUsers: List<BlockedUserEntity> = emptyList(),
)