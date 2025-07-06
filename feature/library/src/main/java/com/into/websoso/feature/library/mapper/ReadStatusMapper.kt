package com.into.websoso.feature.library.mapper

import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.feature.library.model.ReadStatusUiModel

fun ReadStatus.toUiModel(): ReadStatusUiModel =
    when (this) {
        ReadStatus.WATCHING -> ReadStatusUiModel.WATCHING
        ReadStatus.WATCHED -> ReadStatusUiModel.WATCHED
        ReadStatus.QUIT -> ReadStatusUiModel.QUIT
    }
