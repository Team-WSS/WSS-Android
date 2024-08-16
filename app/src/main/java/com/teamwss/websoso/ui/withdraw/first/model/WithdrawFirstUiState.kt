package com.teamwss.websoso.ui.withdraw.first.model

data class WithdrawFirstUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val userNovelStats: UserNovelStatsModel = UserNovelStatsModel(),
)
