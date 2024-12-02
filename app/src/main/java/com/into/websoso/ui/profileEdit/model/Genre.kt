package com.into.websoso.ui.profileEdit.model

enum class Genre(val krName: String, val tag: String) {
    ROMANCE("로맨스", "romance"),
    ROMANCE_FANTASY("로판", "romanceFantasy"),
    FANTASY("판타지", "fantasy"),
    MODERN_FANTASY("현판", "modernFantasy"),
    WUXIA("무협", "wuxia"),
    BL("BL", "BL"),
    LIGHT_NOVEL("라노벨", "lightNovel"),
    MYSTERY("미스터리", "mystery"),
    DRAMA("드라마", "drama");

    companion object {
        fun String.toGenreFromTag(): Genre {
            return entries.find { it.tag == this }
                ?: throw IllegalArgumentException("Invalid genre tag")
        }

        fun String.toGenreFromKr(): Genre {
            return entries.find { it.krName == this }
                ?: throw IllegalArgumentException("Invalid genre krName")
        }

        fun Genre.toTag(): String {
            return tag
        }
    }
}
