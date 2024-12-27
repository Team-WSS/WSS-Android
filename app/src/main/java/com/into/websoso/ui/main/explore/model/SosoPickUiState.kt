package com.into.websoso.ui.main.explore.model

import com.into.websoso.data.model.SosoPickEntity

data class SosoPickUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val sosoPicks: List<SosoPickEntity.NovelEntity> = emptyList(),
)