package com.into.websoso.feature.library.mapper

import com.into.websoso.domain.library.model.AttractivePoint
import com.into.websoso.feature.library.model.AttractivePointUiModel

internal fun AttractivePoint.toUiModel(): AttractivePointUiModel =
    AttractivePointUiModel(
        type = this,
        label = this.label,
        key = this.key,
    )
