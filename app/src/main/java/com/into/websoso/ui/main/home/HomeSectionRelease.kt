package com.into.websoso.ui.main.home

import com.into.websoso.ui.main.home.model.HomeUiState

/** Main-thread only. Upper successes open Home once; subsequent updates retain content. */
internal class HomeSectionRelease {
    var pending = HomeUiState()
        private set
    private val waitingForUpper = mutableSetOf(HomeSection.POPULAR, HomeSection.FEEDS)

    fun update(
        section: HomeSection,
        state: HomeUiState,
    ): HomeUiState? {
        pending = state
        waitingForUpper.remove(section)
        if (waitingForUpper.isNotEmpty()) return null
        pending = state.copy(loading = false)
        return pending
    }
}
