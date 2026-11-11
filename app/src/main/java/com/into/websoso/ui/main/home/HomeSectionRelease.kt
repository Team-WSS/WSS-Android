package com.into.websoso.ui.main.home

/** Main-thread only. Tracks when Home can open; content is owned by HomeViewModel. */
internal class HomeSectionRelease {
    private val waitingForUpper = mutableSetOf(HomeSection.POPULAR, HomeSection.FEEDS)

    fun update(section: HomeSection): Boolean {
        waitingForUpper.remove(section)
        return waitingForUpper.isEmpty()
    }

    fun recover(loading: Boolean) {
        if (!loading) waitingForUpper.clear()
    }
}
