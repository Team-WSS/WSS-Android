package com.into.websoso.feature.feed.model

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
import com.into.websoso.core.designsystem.theme.Transparent
import com.into.websoso.core.designsystem.theme.Violet

enum class NovelCategory(
    val boxColor: Color,
    val iconColor: Color,
    val tag: String,
    val koreanName: String,
) {
    ROMANCE(LightPink, Pink, "romance", "로맨스"),
    ROMANCE_FANTASY(LightRed, Red, "romanceFantasy", "로판"),
    FANTASY(LightPurple, Purple, "fantasy", "판타지"),
    MODERN_FANTASY(LightBlue, Blue, "modernFantasy", "현판"),
    WUXIA(LightOrange, Orange, "wuxia", "무협"),
    MYSTERY(LightViolet, Violet, "mystery", "미스테리"),
    DRAMA(LightGray, Gray, "drama", "드라마"),
    LIGHT_NOVEL(LightGreen, Green, "lightNovel", "라노벨"),
    BOYS_LOVE(LightMint, Mint, "BL", "BL"),
    ETC(Transparent, Transparent, "etc", "그 외"),
    ;

    companion object {
        fun fromTag(tag: String): NovelCategory = entries.find { it.tag == tag } ?: ETC
    }
}
