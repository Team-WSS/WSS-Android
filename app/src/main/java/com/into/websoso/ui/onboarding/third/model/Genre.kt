package com.into.websoso.ui.onboarding.third.model

import com.into.websoso.core.resource.R.drawable.ic_onboarding_bl
import com.into.websoso.core.resource.R.drawable.ic_onboarding_drama
import com.into.websoso.core.resource.R.drawable.ic_onboarding_fantasy
import com.into.websoso.core.resource.R.drawable.ic_onboarding_light_novel
import com.into.websoso.core.resource.R.drawable.ic_onboarding_modern_fantasy
import com.into.websoso.core.resource.R.drawable.ic_onboarding_mystery
import com.into.websoso.core.resource.R.drawable.ic_onboarding_romance
import com.into.websoso.core.resource.R.drawable.ic_onboarding_romance_fantasy
import com.into.websoso.core.resource.R.drawable.ic_onboarding_wuxia

enum class Genre(
    val displayName: String,
    val tag: String,
    val drawableRes: Int,
) {
    ROMANCE("로맨스", "romance", ic_onboarding_romance),
    ROMANCE_FANTASY("로판", "romanceFantasy", ic_onboarding_romance_fantasy),
    BL("BL", "BL", ic_onboarding_bl),
    FANTASY("판타지", "fantasy", ic_onboarding_fantasy),
    MODERN_FANTASY("현판", "modernFantasy", ic_onboarding_modern_fantasy),
    WUXIA("무협", "wuxia", ic_onboarding_wuxia),
    LIGHT_NOVEL("라이트노벨", "lightNovel", ic_onboarding_light_novel),
    DRAMA("드라마", "drama", ic_onboarding_drama),
    MYSTERY("미스터리", "mystery", ic_onboarding_mystery),
}
