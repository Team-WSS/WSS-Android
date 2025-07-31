package com.into.websoso.feature.library.model

import com.into.websoso.domain.library.model.AttractivePoint

data class AttractivePointUiModel(
    val type: AttractivePoint,
    val label: String,
    val key: String,
)
