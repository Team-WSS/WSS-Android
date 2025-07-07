package com.into.websoso.feature.library.model

import com.into.websoso.domain.library.model.AttractivePoints

internal data class AttractivePointUiModel(
    val type: AttractivePoints,
    val label: String,
    val key: String,
)
