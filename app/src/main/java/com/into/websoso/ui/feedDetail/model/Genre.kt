package com.into.websoso.ui.feedDetail.model

import androidx.annotation.DrawableRes
import com.into.websoso.R

enum class Genre(
    val tag: String,
    @DrawableRes val drawableRes: Int,
) {
    ROMANCE("romance", R.drawable.ic_romance),
    ROMANCE_FANTASY("romanceFantasy", R.drawable.ic_romance_fantasy),
    BL("BL", R.drawable.ic_bl),
    FANTASY("fantasy", R.drawable.ic_fantasy),
    MODERN_FANTASY(tag = "modernFantasy", drawableRes = R.drawable.ic_hf),
    WUXIA("wuxia", R.drawable.ic_wuxia),
    LIGHT_NOVEL(
        "lightNovel",
        R.drawable.ic_ln,
    ),
    DRAMA("drama", R.drawable.ic_drama),
    MYSTERY("mystery", R.drawable.ic_mystery),
    ;

    companion object {
        fun from(tag: String): Genre = entries.find { it.tag == tag } ?: ROMANCE
    }
}
