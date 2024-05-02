package com.teamwss.websoso.ui.main.explore.model

data class SosoPickUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val sosoPicks: List<SosoPickModel> = emptyList(),
)

data class SosoPickModel(
    val novelId: Long,
    val novelTitle: String,
    val novelCover: String,
)