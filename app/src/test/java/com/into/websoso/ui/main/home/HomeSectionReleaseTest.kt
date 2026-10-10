package com.into.websoso.ui.main.home

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeSectionReleaseTest {
    @Test
    fun `upper content waits for both upper results but never for taste`() {
        listOf(HomeSection.POPULAR, HomeSection.FEEDS).forEach { first ->
            val release = HomeSectionRelease()
            val second = if (first == HomeSection.POPULAR) HomeSection.FEEDS else HomeSection.POPULAR

            assertFalse(release.update(first))
            assertFalse(release.update(first))
            assertTrue(release.update(second))
        }
    }

    @Test
    fun `taste updates never open Home before both upper results`() {
        val release = HomeSectionRelease()

        assertFalse(release.update(HomeSection.TASTE))
        assertFalse(release.update(HomeSection.FEEDS))
        assertFalse(release.update(HomeSection.TASTE))
        assertTrue(release.update(HomeSection.POPULAR))
    }

    @Test
    fun `all section updates remain released once Home opens`() {
        val release = HomeSectionRelease()
        release.update(HomeSection.POPULAR)
        release.update(HomeSection.FEEDS)

        HomeSection.entries.forEach { section ->
            assertTrue(release.update(section))
        }
    }

    @Test
    fun `recovery displaying partial content releases later section updates`() {
        val release = HomeSectionRelease()
        assertFalse(release.update(HomeSection.FEEDS))

        release.recover(loading = false)

        assertTrue(release.update(HomeSection.TASTE))
        assertTrue(release.update(HomeSection.FEEDS))
    }

    @Test
    fun `recovery during initial loading keeps waiting for the missing upper result`() {
        val release = HomeSectionRelease()
        assertFalse(release.update(HomeSection.FEEDS))

        release.recover(loading = true)

        assertFalse(release.update(HomeSection.TASTE))
        assertFalse(release.update(HomeSection.FEEDS))
        assertTrue(release.update(HomeSection.POPULAR))
    }

    @Test
    fun `a new Home owner starts with its own upper wait`() {
        val previous = HomeSectionRelease()
        previous.recover(loading = false)
        assertTrue(previous.update(HomeSection.TASTE))

        val next = HomeSectionRelease()

        assertFalse(next.update(HomeSection.FEEDS))
        assertFalse(next.update(HomeSection.TASTE))
        assertTrue(next.update(HomeSection.POPULAR))
    }
}
