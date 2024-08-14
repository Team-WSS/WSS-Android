package com.teamwss.websoso.ui.blockUsers.model

import com.teamwss.websoso.ui.blockUsers.model.BlockUsersModel.BlockUserModel

data class BlockUsersUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val blockUsers: List<BlockUserModel> = emptyList(),
)