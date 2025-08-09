package com.into.websoso.feature.library.model

import androidx.compose.ui.graphics.Color
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.domain.library.model.ReadStatus

enum class ReadStatusUiModel(
    val readStatus: ReadStatus,
    val backgroundColor: Color,
) {
    WATCHING(ReadStatus.WATCHING, Primary100),
    WATCHED(ReadStatus.WATCHED, Black),
    QUIT(ReadStatus.QUIT, Gray200),
    ;

    companion object {
        fun from(readStatus: ReadStatus?): ReadStatusUiModel? = entries.find { it.readStatus == readStatus }
    }
}
