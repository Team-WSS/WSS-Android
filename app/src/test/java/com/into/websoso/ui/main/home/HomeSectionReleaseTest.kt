package com.into.websoso.ui.main.home

import com.into.websoso.data.model.PopularFeedEntity
import com.into.websoso.data.model.PopularNovelsEntity.PopularNovelEntity
import com.into.websoso.data.model.RecommendedNovelsByUserTasteEntity.RecommendedNovelByUserTasteEntity
import com.into.websoso.ui.main.home.model.HomeTasteStatus
import com.into.websoso.ui.main.home.model.HomeUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class HomeSectionReleaseTest {
    @Test
    fun `upper content waits for both upper results but never for taste`() {
        val release = HomeSectionRelease()
        assertNull(
            release.update(
                HomeSection.POPULAR,
                release.pending.copy(popularNovels = popular),
            ),
        )

        val state = release.update(HomeSection.FEEDS, release.pending.copy(popularFeeds = feeds))!!

        assertFalse(state.loading)
        assertEquals(HomeTasteStatus.LOADING, state.tasteStatus)
        assertEquals(popular, state.popularNovels)
        assertEquals(feeds, state.popularFeeds)
    }

    @Test
    fun `all completion orders publish upper content and retain ten taste novels`() {
        listOf("pft", "ptf", "fpt", "ftp", "tpf", "tfp").forEach { order ->
            val release = HomeSectionRelease()
            val displayed = mutableListOf<HomeUiState>()
            order.forEach { section ->
                val state = release.pending
                val result = when (section) {
                    'p' -> release.update(HomeSection.POPULAR, state.copy(popularNovels = popular))

                    'f' -> release.update(HomeSection.FEEDS, state.copy(popularFeeds = feeds))

                    else -> release.update(
                        HomeSection.TASTE,
                        state.copy(
                            tasteStatus = HomeTasteStatus.CONTENT,
                            recommendedNovelsByUserTaste = taste,
                        ),
                    )
                }
                result?.let(displayed::add)
            }

            assertEquals(order, if (order.last() == 't') 2 else 1, displayed.size)
            assertEquals(10, displayed.last().recommendedNovelsByUserTaste.size)
            assertEquals(popular, displayed.first().popularNovels)
            assertEquals(feeds, displayed.first().popularFeeds)
        }
    }

    @Test
    fun `early taste failure stays buffered until both upper results succeed`() {
        val release = HomeSectionRelease()
        assertNull(
            release.update(
                HomeSection.TASTE,
                release.pending.copy(tasteStatus = HomeTasteStatus.ERROR),
            ),
        )
        assertNull(release.update(HomeSection.FEEDS, release.pending.copy(popularFeeds = feeds)))

        val state =
            release.update(HomeSection.POPULAR, release.pending.copy(popularNovels = popular))!!

        assertFalse(state.error)
        assertFalse(state.loading)
        assertEquals(HomeTasteStatus.ERROR, state.tasteStatus)
    }

    @Test
    fun `error retry and empty recovery keep upper list identities`() {
        val release = readyUpper()
        val initial = release.pending
        listOf(
            HomeTasteStatus.ERROR,
            HomeTasteStatus.LOADING,
            HomeTasteStatus.EMPTY,
        ).forEach { status ->
            val state =
                release.update(HomeSection.TASTE, release.pending.copy(tasteStatus = status))!!

            assertFalse(state.loading)
            assertFalse(state.error)
            assertSame(initial.popularNovels, state.popularNovels)
            assertSame(initial.popularFeeds, state.popularFeeds)
            assertEquals(status, state.tasteStatus)
        }
    }

    @Test
    fun `feed and novel refreshes keep published taste and the other upper list`() {
        val release = readyUpper()
        release.update(
            HomeSection.TASTE,
            release.pending.copy(
                tasteStatus = HomeTasteStatus.CONTENT,
                recommendedNovelsByUserTaste = taste,
            ),
        )
        val refreshedFeeds = feeds.map { page -> page.map { it.copy(likeCount = 2) } }
        val feedState =
            release.update(HomeSection.FEEDS, release.pending.copy(popularFeeds = refreshedFeeds))!!
        assertSame(popular, feedState.popularNovels)
        assertSame(taste, feedState.recommendedNovelsByUserTaste)

        val refreshedPopular = popular.map { it.copy(title = "updated") }
        val novelState = release.update(
            HomeSection.POPULAR,
            release.pending.copy(popularNovels = refreshedPopular),
        )!!
        assertSame(refreshedFeeds, novelState.popularFeeds)
        assertSame(taste, novelState.recommendedNovelsByUserTaste)
        assertFalse(novelState.loading)
    }

    @Test
    fun `a new Home owner starts with loading rather than previous content or empty`() {
        val previous = readyUpper()
        previous.update(
            HomeSection.TASTE,
            previous.pending.copy(tasteStatus = HomeTasteStatus.EMPTY),
        )
        val next = HomeSectionRelease()

        assertEquals(HomeUiState(), next.pending)
        assertNull(next.update(HomeSection.FEEDS, next.pending.copy(popularFeeds = feeds)))
    }

    private fun readyUpper(): HomeSectionRelease =
        HomeSectionRelease().apply {
            update(HomeSection.POPULAR, pending.copy(popularNovels = popular))
            update(HomeSection.FEEDS, pending.copy(popularFeeds = feeds))
        }

    private val popular = listOf(
        PopularNovelEntity(
            "author",
            null,
            null,
            "fantasy",
            false,
            emptyList(),
            null,
            "description",
            1,
            "image",
            "title",
        ),
    )
    private val feeds = listOf(
        listOf(
            PopularFeedEntity(
                1,
                "content",
                0,
                0,
                false,
                true,
                "title",
                "image",
                "fantasy",
            ),
        ),
    )
    private val taste = (1L..10L).map {
        RecommendedNovelByUserTasteEntity(
            it,
            "title $it",
            "author",
            "image",
            0,
            0.0,
            0,
        )
    }
}
