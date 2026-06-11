package com.into.websoso.ui.main.home.adpater

internal fun String.toPopularNovelGenreImagePath(): String =
    when (this) {
        "romance" -> "/icGenre/romance"
        "romanceFantasy" -> "/icGenre/romance-fantasy"
        "BL" -> "/icGenre/bl"
        "fantasy" -> "/icGenre/fantasy"
        "modernFantasy" -> "/icGenre/modern-fantasy"
        "wuxia" -> "/icGenre/wuxia"
        "lightNovel" -> "/icGenre/light-novel"
        "drama" -> "/icGenre/drama"
        "mystery" -> "/icGenre/mystery"
        else -> ""
    }
