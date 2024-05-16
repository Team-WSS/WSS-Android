package com.teamwss.websoso.ui.onBoarding.first.model

import com.teamwss.websoso.R

data class OnBoardingFirstUIResources(
    val editTextBackgroundRes: Int,
    val clearIconRes: Int,
    val messageColorRes: Int,
)

val onBoardingFirstUiResourcesMap = mapOf(
    NicknameInputType.INITIAL to OnBoardingFirstUIResources(
        R.drawable.bg_on_boarding_gray50_radius_12dp,
        R.drawable.ic_on_boarding_first_input_clear_default,
        R.color.secondary_100_FF675D
    ),
    NicknameInputType.TYPING to OnBoardingFirstUIResources(
        R.drawable.bg_on_boarding_white_radius_12dp_border_gray70_1dp,
        R.drawable.ic_on_boarding_first_input_clear_default,
        R.color.secondary_100_FF675D
    ),
    NicknameInputType.ERROR to OnBoardingFirstUIResources(
        R.drawable.bg_on_boarding_white_radius_12dp_border_secondary100_1dp,
        R.drawable.ic_on_boarding_first_input_clear_error,
        R.color.secondary_100_FF675D
    ),
    NicknameInputType.COMPLETE to OnBoardingFirstUIResources(
        R.drawable.bg_on_boarding_white_radius_12dp_border_primary100_1dp,
        R.drawable.ic_on_boarding_first_input_clear_complete,
        R.color.primary_100_6A5DFD
    )
)
