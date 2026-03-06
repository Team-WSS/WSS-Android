package com.into.websoso.ui.novelDetail.model

import androidx.annotation.ColorRes
import com.into.websoso.R

enum class Category(
    val titleKr: String,
    val titleEn: String,
    @ColorRes val iconColor: Int,
    @ColorRes val backgroundColor: Int,
) {
    ROMANCE("로맨스", "romance", R.color.romance_E586CA, R.color.romance_FFF5FC),
    ROMANCE_FANTASY(
        "로판",
        "romanceFantasy",
        R.color.romance_fantasy_B72F58,
        R.color.romance_fantasy_F9EBE9,
    ),
    BOYS_LOVE("BL", "bl", R.color.bl_021A6FF, R.color.bl_F0FAFD),
    FANTASY("판타지", "fantasy", R.color.fantasy_6457FC, R.color.fantasy_F0F1FF),
    MODERN_FANTASY(
        "현판",
        "modernFantasy",
        R.color.modern_fantasy_4767F7,
        R.color.modern_fantasy_E9F2FF,
    ),
    WUXIA("무협", "wuxia", R.color.wuxia_B8896E, R.color.wuxia_FDF2E3),
    DRAMA("드라마", "drama", R.color.drama_5D78A0, R.color.drama_EEF0F7),
    MYSTERY("미스터리", "mystery", R.color.mystery_9560BF, R.color.mystery_F2E6F2),
    LIGHT_NOVEL("라노벨", "lightNovel", R.color.light_novel_AEC23B, R.color.light_novel_EFF3D6),
    ETC("기타", "etc", R.color.etc_6457FC, R.color.etc_FFFFFF),
    ;

    companion object {
        fun from(title: String): Category =
            entries.find { category ->
                title == category.titleEn || title == category.titleKr
            } ?: throw IllegalArgumentException("존재하지 않는 장르입니다: $title")
    }
}
