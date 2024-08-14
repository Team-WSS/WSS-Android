package com.teamwss.websoso.ui.blockedUsers.model

import com.teamwss.websoso.ui.blockedUsers.model.BlockedUsersModel.BlockedUserModel

data class BlockedUsersUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val blockedUsers: List<BlockedUserModel> = emptyList(),
)