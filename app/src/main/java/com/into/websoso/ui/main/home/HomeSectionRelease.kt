package com.into.websoso.ui.main.home

import com.into.websoso.ui.main.home.model.HomeUiState

/** Main-thread only. Upper successes or recovery displaying content open Home once. */
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

    fun recover(loading: Boolean): HomeUiState {
        if (!loading) waitingForUpper.clear()
        pending = pending.copy(loading = loading)
        return pending
    }
}
