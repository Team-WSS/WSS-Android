package com.into.websoso.feature.feed.model

import androidx.annotation.ColorRes
import androidx.compose.ui.graphics.Color
import com.into.websoso.core.designsystem.theme.Blue
import com.into.websoso.core.designsystem.theme.Gray
import com.into.websoso.core.designsystem.theme.Green
import com.into.websoso.core.designsystem.theme.LightBlue
import com.into.websoso.core.designsystem.theme.LightGray
import com.into.websoso.core.designsystem.theme.LightGreen
import com.into.websoso.core.designsystem.theme.LightMint
import com.into.websoso.core.designsystem.theme.LightOrange
import com.into.websoso.core.designsystem.theme.LightPink
import com.into.websoso.core.designsystem.theme.LightPurple
import com.into.websoso.core.designsystem.theme.LightRed
import com.into.websoso.core.designsystem.theme.LightViolet
import com.into.websoso.core.designsystem.theme.Mint
import com.into.websoso.core.designsystem.theme.Orange
import com.into.websoso.core.designsystem.theme.Pink
import com.into.websoso.core.designsystem.theme.Purple
import com.into.websoso.core.designsystem.theme.Red
import com.into.websoso.core.designsystem.theme.Violet

enum class NovelCategory(
    @ColorRes val boxColor: Color,
    @ColorRes val iconColor: Color,
    val tag: String,
) {
    LIGHT_NOVEL(LightGreen, Green, "lightNovel"),
    WUXIA(LightOrange, Orange, "wuxia"),
    FANTASY(LightPurple, Purple, "fantasy"),
    ROMANCE(LightPink, Pink, "romance"),
    BOYS_LOVE(LightMint, Mint, "BL"),
    ROMANCE_FANTASY(LightRed, Red, "romanceFantasy"),
    MODERN_FANTASY(LightBlue, Blue, "modernFantasy"),
    DRAMA(LightGray, Gray, "drama"),
    MYSTERY(LightViolet, Violet, "mystery"),
    ;

    companion object {
        fun fromTag(tag: String): NovelCategory {
            return entries.find { it.tag == tag }
                ?: LIGHT_NOVEL
        }
    }
}
