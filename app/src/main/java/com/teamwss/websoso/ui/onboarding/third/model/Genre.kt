package com.teamwss.websoso.ui.onboarding.third.model

import com.teamwss.websoso.R

enum class Genre(val displayName: String, val tag: String, val drawableRes: Int) {
    ROMANCE("로맨스", "romance", R.drawable.ic_onboarding_romance),
    ROMANCE_FANTASY("로판", "romanceFantasy", R.drawable.ic_onboarding_romance_fantasy),
    BL("BL", "BL", R.drawable.ic_onboarding_bl),
    FANTASY("판타지", "fantasy", R.drawable.ic_onboarding_fantasy),
    MODERN_FANTASY("현판", "modernFantasy", R.drawable.ic_onboarding_modern_fantasy),
    WUXIA("무협", "wuxia", R.drawable.ic_onboarding_wuxia),
    LIGHT_NOVEL("라이트노벨", "lightNovel", R.drawable.ic_onboarding_light_novel),
    DRAMA("드라마", "drama", R.drawable.ic_onboarding_drama),
    MYSTERY("미스터리", "mystery", R.drawable.ic_onboarding_mystery);
}
