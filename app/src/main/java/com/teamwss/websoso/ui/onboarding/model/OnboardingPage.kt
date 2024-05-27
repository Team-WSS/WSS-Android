package com.teamwss.websoso.ui.onboarding.model

enum class OnboardingPage(val progressPercent: Int, val isBackButtonVisible: Boolean, val isSkipTextVisible: Boolean) {
    FIRST(33, false, false),
    SECOND(66, true, false),
    THIRD(100, true, true);

    fun nextPage(): OnboardingPage? {
        return values().getOrNull(this.ordinal + 1)
    }

    fun previousPage(): OnboardingPage? {
        return values().getOrNull(this.ordinal - 1)
    }
}
