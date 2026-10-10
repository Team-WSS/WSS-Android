package com.into.websoso.ui.main.home

import com.into.websoso.data.model.PopularFeedEntity
import com.into.websoso.data.model.PopularNovelsEntity
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class HomeStartupRequestTest {
    private val dispatcher = StandardTestDispatcher()
    private var identity = "session-a"
    private val popular = PopularNovelsEntity(emptyList())
    private val feeds = emptyList<PopularFeedEntity>()

    @Test
    fun `completed responses are delivered once and unrelated IDs cannot claim them`() =
        runTest(dispatcher) {
            var calls = 0
            val startup = HomeStartupRequest(
                { identity },
                {
                    calls++
                    popular
                },
                {
                    calls++
                    feeds
                },
                dispatcher,
            )
            val id = startup.start()
            runCurrent()
            assertEquals(2, calls)
            assertNull(startup.take("unrelated"))
            val requests = startup.take(id)!!
            assertNull(startup.take(id))
            assertSame(popular, requests.popular())
            assertSame(feeds, requests.feeds())
            assertEquals(2, calls)
        }

    @Test
    fun `session changes during handoff reject both an old result and an old failure`() =
        runTest(dispatcher) {
            listOf(false, true).forEach { fail ->
                identity = "session-a"
                val response = CompletableDeferred<PopularNovelsEntity>()
                val startup = HomeStartupRequest({ identity }, { response.await() }, { feeds }, dispatcher)
                val requests = startup.take(startup.start())!!
                val result = async { requests.popular() }
                runCurrent()
                identity = "session-b"
                if (fail) response.completeExceptionally(IOException("synthetic")) else response.complete(popular)
                runCurrent()
                assertNull(result.await())
                assertNull(requests.feeds())
            }
        }

    @Test
    fun `session lookup failures after handoff fall back instead of surfacing a storage error`() =
        runTest(dispatcher) {
            // Lookup 1 is start(); 2 checks before waiting, 3 after the response, 4 in the failure path.
            listOf(setOf(2), setOf(3, 4)).forEach { failingLookups ->
                var lookups = 0
                val startup = HomeStartupRequest(
                    { if (++lookups in failingLookups) throw IOException("synthetic storage failure") else identity },
                    { popular },
                    { feeds },
                    dispatcher,
                )
                val requests = startup.take(startup.start())!!
                runCurrent()
                val result = runCatching { requests.popular() }
                assertTrue("$failingLookups: ${result.exceptionOrNull()}", result.isSuccess && result.getOrNull() == null)
            }
        }

    @Test
    fun `consumer cancellation cancels its in flight request without cancelling its sibling`() =
        runTest(dispatcher) {
            var popularCancelled = false
            var feedsCancelled = false
            val startup = HomeStartupRequest(
                { identity },
                {
                    try {
                        CompletableDeferred<PopularNovelsEntity>().await()
                    } finally {
                        popularCancelled = true
                    }
                },
                {
                    try {
                        CompletableDeferred<List<PopularFeedEntity>>().await()
                    } finally {
                        feedsCancelled = true
                    }
                },
                dispatcher,
            )
            val requests = startup.take(startup.start())!!
            val consumer = async { requests.popular() }
            runCurrent()
            consumer.cancel()
            runCurrent()
            assertTrue(consumer.isCancelled)
            assertTrue(popularCancelled)
            assertFalse(feedsCancelled)
            requests.cancel()
            runCurrent()
            assertTrue(feedsCancelled)
        }

    @Test
    fun `new startup cancels unclaimed work and discarding an old ID leaves the new work alone`() =
        runTest(dispatcher) {
            var cancelled = 0
            val startup = HomeStartupRequest(
                { identity },
                {
                    try {
                        CompletableDeferred<PopularNovelsEntity>().await()
                    } finally {
                        cancelled++
                    }
                },
                { feeds },
                dispatcher,
            )
            val first = startup.start()
            runCurrent()
            val second = startup.start()
            runCurrent()
            assertEquals(1, cancelled)
            assertNull(startup.take(first))
            startup.discard(first)
            runCurrent()
            assertEquals(1, cancelled)
            startup.discard(second)
            runCurrent()
            assertEquals(2, cancelled)
        }

    @Test
    fun `missing session or failed session lookup never starts content requests`() =
        runTest(dispatcher) {
            listOf<suspend () -> String>({ "" }, { throw IOException("synthetic") }).forEach { session ->
                val startup = HomeStartupRequest(session, { error("unexpected request") }, { error("unexpected request") }, dispatcher)
                assertNull(startup.start())
                runCurrent()
            }
        }

    @Test
    fun `a superseded slow session lookup cannot replace a newer startup`() =
        runTest(dispatcher) {
            val session = CompletableDeferred<String>()
            var lookups = 0
            val startup = HomeStartupRequest(
                { if (++lookups == 1) session.await() else identity },
                { popular },
                { feeds },
                dispatcher,
            )
            val first = async { startup.start() }
            runCurrent()
            val second = startup.start()
            session.complete(identity)
            runCurrent()
            assertNull(first.await())
            assertSame(popular, startup.take(second)!!.popular())
        }
}
