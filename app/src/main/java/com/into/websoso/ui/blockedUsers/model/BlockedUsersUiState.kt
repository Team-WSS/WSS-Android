package com.into.websoso.ui.blockedUsers.model

import com.into.websoso.data.model.BlockedUsersEntity.BlockedUserEntity

data class BlockedUsersUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val blockedUsers: List<BlockedUserEntity> = emptyList(),
)
