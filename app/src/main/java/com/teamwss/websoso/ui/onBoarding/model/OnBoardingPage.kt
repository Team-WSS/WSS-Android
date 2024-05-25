package com.teamwss.websoso.ui.onBoarding.model

enum class OnBoardingPage(val progressPercent: Int, val isBackButtonVisible: Boolean, val isSkipTextVisible: Boolean) {
    FIRST(33, false, false),
    SECOND(66, true, false),
    THIRD(100, true, true);

    fun nextPage(): OnBoardingPage? {
        return values().getOrNull(this.ordinal + 1)
    }

    fun previousPage(): OnBoardingPage? {
        return values().getOrNull(this.ordinal - 1)
    }
}
