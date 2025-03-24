package com.into.websoso.ui.onboarding.first.model

import com.into.websoso.R.color.primary_100_6A5DFD
import com.into.websoso.R.color.secondary_100_FF675D
import com.into.websoso.R.drawable.bg_onboarding_gray50_radius_12dp
import com.into.websoso.R.drawable.bg_onboarding_white_stroke_gray_70_radius_12dp
import com.into.websoso.R.drawable.bg_onboarding_white_stroke_primary_100_radius_12dp
import com.into.websoso.R.drawable.bg_onboarding_white_stroke_secondary_100_radius_12dp
import com.into.websoso.resource.R.drawable.ic_onboarding_first_input_clear_complete
import com.into.websoso.resource.R.drawable.ic_onboarding_first_input_clear_default
import com.into.websoso.resource.R.drawable.ic_onboarding_first_input_clear_error

data class OnboardingFirstUiModel(
    val editTextBackgroundRes: Int,
    val clearIconRes: Int,
    val messageColorRes: Int,
)

val onboardingFirstUiModelMap = mapOf(
    NicknameInputType.INITIAL to OnboardingFirstUiModel(
        bg_onboarding_gray50_radius_12dp,
        ic_onboarding_first_input_clear_default,
        secondary_100_FF675D,
    ),
    NicknameInputType.TYPING to OnboardingFirstUiModel(
        bg_onboarding_white_stroke_gray_70_radius_12dp,
        ic_onboarding_first_input_clear_default,
        secondary_100_FF675D,
    ),
    NicknameInputType.ERROR to OnboardingFirstUiModel(
        bg_onboarding_white_stroke_secondary_100_radius_12dp,
        ic_onboarding_first_input_clear_error,
        secondary_100_FF675D,
    ),
    NicknameInputType.COMPLETE to OnboardingFirstUiModel(
        bg_onboarding_white_stroke_primary_100_radius_12dp,
        ic_onboarding_first_input_clear_complete,
        primary_100_6A5DFD,
    ),
)
