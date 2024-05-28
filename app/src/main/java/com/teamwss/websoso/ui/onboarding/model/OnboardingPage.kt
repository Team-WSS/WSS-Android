package com.teamwss.websoso.ui.onboarding.model

data class OnboardingPage(
    val progressPercent: Int,
    val isBackButtonVisible: Boolean,
    val isSkipTextVisible: Boolean
) {
    companion object {
        val FIRST = OnboardingPage(
            progressPercent = 33,
            isBackButtonVisible = false,
            isSkipTextVisible = false
        )
        val SECOND = OnboardingPage(
            progressPercent = 66,
            isBackButtonVisible = true,
            isSkipTextVisible = false
        )
        val THIRD = OnboardingPage(
            progressPercent = 100,
            isBackButtonVisible = true,
            isSkipTextVisible = true
        )

        val pages = listOf(FIRST, SECOND, THIRD)
    }
}
