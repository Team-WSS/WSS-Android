package com.into.websoso.ui.main.home

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class HomeSectionLoaderTest {
    @Test
    fun `only unauthorized response is a session failure`() {
        fun http(code: Int) = HttpException(Response.error<Unit>(code, "synthetic".toResponseBody()))

        assertTrue(isSessionFailure(http(401)))
        assertFalse(isSessionFailure(http(403)))
        assertFalse(isSessionFailure(http(500)))
        assertFalse(isSessionFailure(IOException("offline")))
    }

    @Test
    fun `retrying failed taste leaves in flight upper requests alone`() =
        runBlocking {
            val loader = HomeSectionLoader(CoroutineScope(coroutineContext + Dispatchers.Unconfined))
            val upper = CompletableDeferred<String>()
            val displayed = mutableListOf<String>()
            val calls = mutableListOf<HomeSection>()
            val pending = listOf(HomeSection.POPULAR, HomeSection.FEEDS).map { section ->
                loader.load(
                    section,
                    {
                        calls.add(section)
                        upper.await()
                    },
                    displayed::add,
                ) { error("unexpected failure") }
            }
            var failures = 0
            loader
                .load(
                    HomeSection.TASTE,
                    {
                        calls.add(HomeSection.TASTE)
                        throw IOException("offline")
                    },
                    displayed::add,
                ) {
                    failures++
                }.join()
            loader
                .load(
                    HomeSection.TASTE,
                    {
                        calls.add(HomeSection.TASTE)
                        "recovered"
                    },
                    displayed::add,
                ) {
                    error("unexpected failure")
                }.join()
            assertEquals(listOf("recovered"), displayed)
            assertEquals(1, failures)
            assertTrue(pending.none { it.isCancelled || it.isCompleted })
            upper.complete("upper")
            pending.forEach { it.join() }
            assertEquals(listOf(HomeSection.POPULAR, HomeSection.FEEDS, HomeSection.TASTE, HomeSection.TASTE), calls)
            assertEquals(3, displayed.size)
        }

    @Test
    fun `ready sections publish before delayed taste`() =
        runBlocking {
            val loader = HomeSectionLoader(CoroutineScope(coroutineContext + Dispatchers.Unconfined))
            val taste = CompletableDeferred<String>()
            val displayed = mutableListOf<String>()
            val waiting = loader.load(HomeSection.TASTE, { taste.await() }, displayed::add) { error("unexpected failure") }
            loader.load(HomeSection.POPULAR, { "popular" }, displayed::add) { error("unexpected failure") }.join()
            loader.load(HomeSection.FEEDS, { "feeds" }, displayed::add) { error("unexpected failure") }.join()
            assertEquals(listOf("popular", "feeds"), displayed)
            assertFalse(waiting.isCompleted)
            taste.complete("taste")
            waiting.join()
            assertEquals(listOf("popular", "feeds", "taste"), displayed)
        }

    @Test
    fun `one request failure does not cancel another section`() =
        runBlocking {
            val loader = HomeSectionLoader(CoroutineScope(coroutineContext + Dispatchers.Unconfined))
            val popular = CompletableDeferred<String>()
            val displayed = mutableListOf<String>()
            val failures = mutableListOf<String>()
            val waiting = loader.load(HomeSection.POPULAR, { popular.await() }, displayed::add) { error("unexpected failure") }
            loader.load(HomeSection.TASTE, { error("synthetic") }, { error("unexpected success") }) { failures.add("taste") }.join()
            assertEquals(listOf("taste"), failures)
            assertFalse(waiting.isCancelled)
            popular.complete("popular")
            waiting.join()
            assertEquals(listOf("popular"), displayed)
        }

    @Test
    fun `superseded response that ignores cancellation cannot overwrite latest result`() =
        runBlocking {
            val loader = HomeSectionLoader(CoroutineScope(coroutineContext + Dispatchers.Unconfined))
            val oldResponse = CompletableDeferred<String>()
            val displayed = mutableListOf<String>()
            val old = loader.load(HomeSection.FEEDS, { withContext(NonCancellable) { oldResponse.await() } }, displayed::add) {
                error("cancelled request must not publish failure")
            }
            loader.load(HomeSection.FEEDS, { "new" }, displayed::add) { error("unexpected failure") }.join()
            oldResponse.complete("old")
            old.join()
            assertTrue(old.isCancelled)
            assertEquals(listOf("new"), displayed)
        }

    @Test
    fun `late failure from superseded request cannot hide new content`() =
        runBlocking {
            val loader = HomeSectionLoader(CoroutineScope(coroutineContext + Dispatchers.Unconfined))
            val gate = CompletableDeferred<Unit>()
            val displayed = mutableListOf<String>()
            val failures = mutableListOf<Exception>()
            val old = loader.load(
                HomeSection.TASTE,
                {
                    withContext(NonCancellable) {
                        gate.await()
                        error("old failure")
                    }
                },
                { error("unexpected success") },
                failures::add,
            )
            loader.load(HomeSection.TASTE, { "new" }, displayed::add, failures::add).join()
            gate.complete(Unit)
            old.join()
            assertEquals(listOf("new"), displayed)
            assertTrue(failures.isEmpty())
        }

    @Test
    fun `leaving Home cancels every owned request without publishing errors`() =
        runBlocking {
            val owner = SupervisorJob()
            val loader = HomeSectionLoader(CoroutineScope(owner + Dispatchers.Unconfined))
            val gate = CompletableDeferred<String>()
            val displayed = mutableListOf<String>()
            val failures = mutableListOf<Exception>()
            val jobs = HomeSection.entries.map { section -> loader.load(section, { gate.await() }, displayed::add, failures::add) }
            owner.cancelAndJoin()
            assertTrue(jobs.all { it.isCancelled })
            assertTrue(displayed.isEmpty())
            assertTrue(failures.isEmpty())
        }
}
