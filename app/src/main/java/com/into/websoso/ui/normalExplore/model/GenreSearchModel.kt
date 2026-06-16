package com.into.websoso.ui.normalExplore.model

import com.into.websoso.core.resource.R.drawable.ic_onboarding_bl
import com.into.websoso.core.resource.R.drawable.ic_onboarding_drama
import com.into.websoso.core.resource.R.drawable.ic_onboarding_fantasy
import com.into.websoso.core.resource.R.drawable.ic_onboarding_light_novel
import com.into.websoso.core.resource.R.drawable.ic_onboarding_modern_fantasy
import com.into.websoso.core.resource.R.drawable.ic_onboarding_mystery
import com.into.websoso.core.resource.R.drawable.ic_onboarding_romance
import com.into.websoso.core.resource.R.drawable.ic_onboarding_romance_fantasy
import com.into.websoso.core.resource.R.drawable.ic_onboarding_wuxia
import com.into.websoso.ui.detailExplore.info.model.Genre

data class GenreSearchModel(
    val genre: Genre,
    val drawableRes: Int,
) {
    val title: String get() = genre.titleKr

    companion object {
        val items: List<GenreSearchModel> = listOf(
            GenreSearchModel(Genre.FANTASY, ic_onboarding_fantasy),
            GenreSearchModel(Genre.MODERN_FANTASY, ic_onboarding_modern_fantasy),
            GenreSearchModel(Genre.ROMANCE_FANTASY, ic_onboarding_romance_fantasy),
            GenreSearchModel(Genre.ROMANCE, ic_onboarding_romance),
            GenreSearchModel(Genre.WUXIA, ic_onboarding_wuxia),
            GenreSearchModel(Genre.BOYS_LOVE, ic_onboarding_bl),
            GenreSearchModel(Genre.LIGHT_NOVEL, ic_onboarding_light_novel),
            GenreSearchModel(Genre.DRAMA, ic_onboarding_drama),
            GenreSearchModel(Genre.MYSTERY, ic_onboarding_mystery),
        )
    }
}
