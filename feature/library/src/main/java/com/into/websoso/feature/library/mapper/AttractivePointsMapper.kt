package com.into.websoso.feature.library.mapper

import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.feature.library.model.AttractivePointUiModel

internal fun AttractivePoints.toUiModel(): AttractivePointUiModel =
    AttractivePointUiModel(
        type = this,
        label = this.label,
        key = this.key,
    )
