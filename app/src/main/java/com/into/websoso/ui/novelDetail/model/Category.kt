package com.into.websoso.ui.novelDetail.model

import androidx.annotation.ColorRes
import com.into.websoso.R

enum class Category(
    val titleKr: String,
    val titleEn: String,
    @ColorRes val iconColor: Int,
    @ColorRes val backgroundColor: Int,
) {
    ROMANCE("로맨스", "romance", R.color.romance_FF3378, R.color.romance_FFE5ED),
    ROMANCE_FANTASY(
        "로판",
        "romanceFantasy",
        R.color.romance_fantasy_873EFF,
        R.color.romance_fantasy_F1E8FF,
    ),
    BOYS_LOVE("BL", "bl", R.color.bl_02A8A4, R.color.bl_E0F6F5),
    FANTASY("판타지", "fantasy", R.color.fantasy_4D63FF, R.color.fantasy_E9ECFF),
    MODERN_FANTASY(
        "현판",
        "modernFantasy",
        R.color.modern_fantasy_525CDE,
        R.color.modern_fantasy_EAEBFF,
    ),
    WUXIA("무협", "wuxia", R.color.wuxia_E05727, R.color.wuxia_FFF1EB),
    DRAMA("드라마", "drama", R.color.drama_02A8A4, R.color.drama_E0F6F5),
    MYSTERY("미스터리", "mystery", R.color.mystery_AD55EC, R.color.mystery_F6EEFC),
    LIGHT_NOVEL("라노벨", "lightNovel", R.color.light_novel_34C2EB, R.color.light_novel_E6F8FD),
    ETC("기타", "etc", R.color.etc_6457FC, R.color.etc_FFFFFF),
    ;

    companion object {
        fun from(title: String): Category =
            entries.find { category ->
                title == category.titleEn || title == category.titleKr
            } ?: throw IllegalArgumentException("존재하지 않는 장르입니다: $title")

        fun iconColor(title: String): Int = from(title).iconColor

        fun backgroundColor(title: String): Int = from(title).backgroundColor
    }
}
