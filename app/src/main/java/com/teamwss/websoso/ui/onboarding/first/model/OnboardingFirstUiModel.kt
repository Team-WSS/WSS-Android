package com.teamwss.websoso.ui.onboarding.first.model

import com.teamwss.websoso.R

data class OnboardingFirstUiModel(
    val editTextBackgroundRes: Int,
    val clearIconRes: Int,
    val messageColorRes: Int,
)

val onboardingFirstUiModelMap = mapOf(
    NicknameInputType.INITIAL to OnboardingFirstUiModel(
        R.drawable.bg_onboarding_gray50_radius_12dp,
        R.drawable.ic_onboarding_first_input_clear_default,
        R.color.secondary_100_FF675D
    ),
    NicknameInputType.TYPING to OnboardingFirstUiModel(
        R.drawable.bg_onboarding_white_radius_12dp_border_gray70_1dp,
        R.drawable.ic_onboarding_first_input_clear_default,
        R.color.secondary_100_FF675D
    ),
    NicknameInputType.ERROR to OnboardingFirstUiModel(
        R.drawable.bg_onboarding_white_radius_12dp_border_secondary100_1dp,
        R.drawable.ic_onboarding_first_input_clear_error,
        R.color.secondary_100_FF675D
    ),
    NicknameInputType.COMPLETE to OnboardingFirstUiModel(
        R.drawable.bg_onboarding_white_radius_12dp_border_primary100_1dp,
        R.drawable.ic_onboarding_first_input_clear_complete,
        R.color.primary_100_6A5DFD
    )
)
