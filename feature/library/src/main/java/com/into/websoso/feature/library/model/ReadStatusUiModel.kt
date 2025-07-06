package com.into.websoso.feature.library.model

import androidx.compose.ui.graphics.Color
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Primary100

enum class ReadStatusUiModel(
    val label: String,
    val backgroundColor: Color,
    val key: String,
) {
    WATCHING("보는중", Primary100, "WATCHING"),
    WATCHED("봤어요", Black, "WATCHED"),
    QUIT("하차", Gray200, "QUIT"),
    ;

    companion object {
        fun from(value: String?): ReadStatusUiModel? = entries.find { it.key == value }
    }
}
