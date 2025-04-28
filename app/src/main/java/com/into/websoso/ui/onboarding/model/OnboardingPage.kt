package com.into.websoso.ui.onboarding.model

class OnboardingPage(
    val progressPercent: Int,
    val isBackButtonVisible: Boolean,
    val isSkipTextVisible: Boolean,
) {
    fun nextPage(): OnboardingPage {
        val currentIndex = pages.indexOf(this)
        return if (currentIndex < pages.size - 1) {
            pages[currentIndex + 1]
        } else {
            this
        }
    }

    fun previousPage(): OnboardingPage {
        val currentIndex = pages.indexOf(this)
        return if (currentIndex > 0) {
            pages[currentIndex - 1]
        } else {
            this
        }
    }

    companion object {
        val FIRST = OnboardingPage(
            progressPercent = 33,
            isBackButtonVisible = false,
            isSkipTextVisible = false,
        )
        val SECOND = OnboardingPage(
            progressPercent = 66,
            isBackButtonVisible = true,
            isSkipTextVisible = false,
        )
        val THIRD = OnboardingPage(
            progressPercent = 100,
            isBackButtonVisible = true,
            isSkipTextVisible = true,
        )

        val pages = listOf(FIRST, SECOND, THIRD)
    }
}
