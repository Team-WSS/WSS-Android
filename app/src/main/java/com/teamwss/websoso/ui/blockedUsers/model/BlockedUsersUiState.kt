package com.teamwss.websoso.ui.blockedUsers.model

import com.teamwss.websoso.ui.blockedUsers.model.BlockedUsersModel.BlockUserModel

data class BlockedUsersUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val blockUsers: List<BlockUserModel> = emptyList(),
)