package com.into.websoso.ui.main.home

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStore
import com.into.websoso.data.remote.api.AuthApi
import com.into.websoso.data.remote.api.FeedApi
import com.into.websoso.data.remote.api.NotificationApi
import com.into.websoso.data.remote.api.NovelApi
import com.into.websoso.data.remote.api.PushMessageApi
import com.into.websoso.data.remote.api.UserApi
import com.into.websoso.data.remote.response.NotificationUnreadResponseDto
import com.into.websoso.data.remote.response.PopularFeedsResponseDto
import com.into.websoso.data.remote.response.PopularFeedsResponseDto.PopularFeedResponseDto
import com.into.websoso.data.remote.response.PopularNovelsResponseDto
import com.into.websoso.data.remote.response.PopularNovelsResponseDto.PopularNovelResponseDto
import com.into.websoso.data.remote.response.RecommendedNovelsByUserTasteResponseDto
import com.into.websoso.data.remote.response.RecommendedNovelsByUserTasteResponseDto.RecommendedNovelByUserTasteResponseDto
import com.into.websoso.data.remote.response.TermsAgreementResponseDto
import com.into.websoso.data.repository.AuthRepository
import com.into.websoso.data.repository.FeedRepository
import com.into.websoso.data.repository.NotificationRepository
import com.into.websoso.data.repository.NovelRepository
import com.into.websoso.data.repository.PushMessageRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.main.home.model.HomeTasteStatus
import com.into.websoso.ui.main.home.model.HomeUiState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Proxy
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val owners = mutableListOf<ViewModelStore>()
    private val calls = mutableMapOf<String, Int>()
    private var tasteRequest: suspend () -> RecommendedNovelsByUserTasteResponseDto = { taste(10) }
    private var popularRequest: suspend () -> PopularNovelsResponseDto = { popular() }
    private var feedRequest: suspend () -> PopularFeedsResponseDto = { feeds() }
    private var notificationRequest: suspend () -> NotificationUnreadResponseDto = { NotificationUnreadResponseDto(true) }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        ArchTaskExecutor.getInstance().setDelegate(
            object : TaskExecutor() {
                override fun executeOnDiskIO(runnable: Runnable) = runnable.run()

                override fun postToMainThread(runnable: Runnable) = runnable.run()

                override fun isMainThread(): Boolean = true
            },
        )
    }

    @After
    fun tearDown() {
        owners.forEach(ViewModelStore::clear)
        Dispatchers.resetMain()
        ArchTaskExecutor.getInstance().setDelegate(null)
    }

    @Test
    fun `actual view model publishes upper before taste and keeps unread notification`() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<RecommendedNovelsByUserTasteResponseDto>()
            tasteRequest = { pending.await() }
            val vm = createViewModel()
            runCurrent()

            val upper = vm.uiState.value!!
            assertFalse(upper.loading)
            assertEquals(1, upper.popularNovels.size)
            assertEquals(HomeTasteStatus.LOADING, upper.tasteStatus)
            assertTrue(upper.isNotificationUnread)

            pending.complete(taste(10))
            runCurrent()
            val complete = vm.uiState.value!!
            assertEquals(10, complete.recommendedNovelsByUserTaste.size)
            assertEquals(HomeTasteStatus.CONTENT, complete.tasteStatus)
            assertTrue(complete.isNotificationUnread)
            assertEquals(false, vm.isNotificationPermissionFirstLaunched.value)
        }

    @Test
    fun `actual retry is taste only rejects duplicate input and recovers empty without hiding upper`() =
        runTest(dispatcher) {
            tasteRequest = { throw http(500) }
            val vm = createViewModel()
            runCurrent()
            val before = vm.uiState.value!!
            assertFalse(before.error)
            assertEquals(HomeTasteStatus.ERROR, before.tasteStatus)
            val upperCalls = calls.filterKeys { it != "getRecommendedNovelsByUserTaste" }
            val pending = CompletableDeferred<RecommendedNovelsByUserTasteResponseDto>()
            tasteRequest = { pending.await() }

            vm.retryTaste()
            vm.retryTaste()
            runCurrent()
            assertEquals(2, calls["getRecommendedNovelsByUserTaste"])
            assertEquals(upperCalls, calls.filterKeys { it != "getRecommendedNovelsByUserTaste" })
            assertSame(before.popularNovels, vm.uiState.value!!.popularNovels)
            assertSame(before.popularFeeds, vm.uiState.value!!.popularFeeds)
            assertFalse(vm.uiState.value!!.loading)
            assertEquals(HomeTasteStatus.LOADING, vm.uiState.value!!.tasteStatus)

            pending.complete(taste(0))
            runCurrent()
            assertEquals(HomeTasteStatus.EMPTY, vm.uiState.value!!.tasteStatus)
            assertFalse(vm.uiState.value!!.error)
        }

    @Test
    fun `updateFeed and updateNovel retain other data and discard replaced taste results`() =
        runTest(dispatcher) {
            val vm = createViewModel()
            runCurrent()
            val initialTaste = vm.uiState.value!!.recommendedNovelsByUserTaste
            vm.updateFeed()
            runCurrent()
            assertSame(initialTaste, vm.uiState.value!!.recommendedNovelsByUserTaste)

            val old = CompletableDeferred<RecommendedNovelsByUserTasteResponseDto>()
            tasteRequest = { withContext(NonCancellable) { old.await() } }
            vm.updateNovel()
            runCurrent()
            assertSame(initialTaste, vm.uiState.value!!.recommendedNovelsByUserTaste)
            assertEquals(HomeTasteStatus.CONTENT, vm.uiState.value!!.tasteStatus)
            assertFalse(vm.uiState.value!!.loading)

            tasteRequest = { taste(1) }
            vm.updateNovel()
            runCurrent()
            old.complete(taste(8))
            runCurrent()
            val latest = vm.uiState.value!!
            assertEquals(1, latest.recommendedNovelsByUserTaste.size)
            assertEquals(HomeTasteStatus.CONTENT, latest.tasteStatus)
            assertEquals(2, calls["getPopularFeeds"])
            assertEquals(3, calls["getPopularNovels"])
        }

    @Test
    fun `authentication failures are global and cannot be cleared by a later content success or retry`() =
        runTest(dispatcher) {
            listOf(401, 403).forEach { code ->
                val upper = CompletableDeferred<PopularNovelsResponseDto>()
                popularRequest = { upper.await() }
                tasteRequest = { throw http(code) }
                val vm = createViewModel()
                runCurrent()
                assertTrue(vm.uiState.value!!.error)
                val count = calls["getRecommendedNovelsByUserTaste"]
                vm.retryTaste()
                upper.complete(popular())
                runCurrent()
                assertTrue(vm.uiState.value!!.error)
                assertEquals(count, calls["getRecommendedNovelsByUserTaste"])
                tasteRequest = { taste(10) }
                vm.updateNovel()
                vm.updateFeed()
                runCurrent()
                assertTrue(vm.uiState.value!!.error)
            }
        }

    @Test
    fun `notification failure remains global while taste and upper responses complete`() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<RecommendedNovelsByUserTasteResponseDto>()
            tasteRequest = { pending.await() }
            notificationRequest = { throw http(401) }
            val vm = createViewModel()
            runCurrent()
            assertTrue(vm.uiState.value!!.error)
            pending.complete(taste(10))
            runCurrent()
            assertTrue(vm.uiState.value!!.error)
            vm.updateFeed()
            runCurrent()
            assertTrue(vm.uiState.value!!.error)
        }

    @Test
    fun `initial complete success and feed refresh recover ordinary global failures but novel and notification success do not`() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<RecommendedNovelsByUserTasteResponseDto>()
            tasteRequest = { pending.await() }
            notificationRequest = { throw http(500) }
            val vm = createViewModel()
            runCurrent()
            assertTrue(vm.uiState.value!!.error)
            pending.complete(taste(10))
            runCurrent()
            val complete = vm.uiState.value!!
            assertFalse(complete.error)
            assertEquals(10, complete.recommendedNovelsByUserTaste.size)

            vm.updateNotificationUnread()
            runCurrent()
            assertTrue(vm.uiState.value!!.error)
            notificationRequest = { NotificationUnreadResponseDto(false) }
            vm.updateNotificationUnread()
            vm.updateNovel()
            runCurrent()
            assertTrue(vm.uiState.value!!.error)
            assertFalse(vm.uiState.value!!.isNotificationUnread)

            vm.updateFeed()
            runCurrent()
            assertFalse(vm.uiState.value!!.error)
            assertFalse(vm.uiState.value!!.isNotificationUnread)
            assertEquals(HomeTasteStatus.CONTENT, vm.uiState.value!!.tasteStatus)
        }

    @Test
    fun `feed failure can recover on next feed success without a global retry button`() =
        runTest(dispatcher) {
            val vm = createViewModel()
            runCurrent()
            feedRequest = { throw http(500) }
            vm.updateFeed()
            runCurrent()
            assertTrue(vm.uiState.value!!.error)
            val tasteBefore = calls["getRecommendedNovelsByUserTaste"]

            feedRequest = { feeds() }
            vm.updateFeed()
            runCurrent()
            val recovered = vm.uiState.value!!
            assertFalse(recovered.error)
            assertEquals(tasteBefore, calls["getRecommendedNovelsByUserTaste"])
            assertTrue(recovered.popularFeeds.isNotEmpty())
        }

    @Test
    fun `feed refresh can recover after failed initial upper request and novel success alone does not recover`() =
        runTest(dispatcher) {
            popularRequest = { throw http(500) }
            val vm = createViewModel()
            runCurrent()
            assertTrue(vm.uiState.value!!.error)

            vm.updateFeed()
            runCurrent()
            val feedRecovered = vm.uiState.value!!
            assertFalse(feedRecovered.error)
            assertFalse(feedRecovered.loading)
            assertTrue(feedRecovered.popularFeeds.isNotEmpty())

            vm.updateNovel()
            runCurrent()
            assertTrue(vm.uiState.value!!.error)
            popularRequest = { popular() }
            vm.updateNovel()
            runCurrent()
            assertTrue(vm.uiState.value!!.error)
            vm.updateFeed()
            runCurrent()
            val complete = vm.uiState.value!!
            assertFalse(complete.error)
            assertTrue(complete.popularNovels.isNotEmpty())
        }

    @Test
    fun `initial taste failure does not count as complete success or let a later taste success clear global error`() =
        runTest(dispatcher) {
            notificationRequest = { throw http(500) }
            tasteRequest = { throw http(500) }
            val vm = createViewModel()
            runCurrent()
            assertTrue(vm.uiState.value!!.error)

            tasteRequest = { taste(10) }
            vm.updateNovel()
            runCurrent()
            assertTrue(vm.uiState.value!!.error)
            vm.updateFeed()
            runCurrent()
            assertFalse(vm.uiState.value!!.error)
            assertEquals(HomeTasteStatus.CONTENT, vm.uiState.value!!.tasteStatus)
        }

    @Test
    fun `replacing an initial request does not turn its cancellation into complete initial success`() =
        runTest(dispatcher) {
            val old = CompletableDeferred<RecommendedNovelsByUserTasteResponseDto>()
            tasteRequest = { withContext(NonCancellable) { old.await() } }
            notificationRequest = { throw http(500) }
            val vm = createViewModel()
            runCurrent()
            tasteRequest = { taste(2) }
            vm.updateNovel()
            runCurrent()
            old.complete(taste(10))
            runCurrent()
            val updated = vm.uiState.value!!
            assertTrue(updated.error)
            assertEquals(2, updated.recommendedNovelsByUserTaste.size)
            vm.updateFeed()
            runCurrent()
            assertFalse(vm.uiState.value!!.error)
        }

    @Test
    fun `same preference empty refresh stays visibly empty until an empty or content response`() =
        runTest(dispatcher) {
            listOf(0, 3).forEach { count ->
                tasteRequest = { taste(0) }
                val vm = createViewModel()
                runCurrent()
                val statuses = mutableListOf<HomeTasteStatus>()
                val observer = Observer<HomeUiState> { statuses.add(it.tasteStatus) }
                vm.uiState.observeForever(observer)
                val pending = CompletableDeferred<RecommendedNovelsByUserTasteResponseDto>()
                tasteRequest = { pending.await() }

                // Background refresh and NovelDetailBack both use the default, unchanged preferences.
                vm.updateNovel()
                runCurrent()
                assertTrue(statuses.all { it == HomeTasteStatus.EMPTY })
                pending.complete(taste(count))
                runCurrent()
                val updated = vm.uiState.value!!
                assertFalse(statuses.contains(HomeTasteStatus.LOADING))
                assertEquals(if (count == 0) HomeTasteStatus.EMPTY else HomeTasteStatus.CONTENT, statuses.last())
                assertEquals(count, updated.recommendedNovelsByUserTaste.size)
                vm.uiState.removeObserver(observer)
            }
        }

    @Test
    fun `profile edit invalidates both empty CTA and existing content while new preferences load`() =
        runTest(dispatcher) {
            listOf(0, 10).forEach { oldCount ->
                tasteRequest = { taste(oldCount) }
                val vm = createViewModel()
                runCurrent()
                val upper = vm.uiState.value!!.popularFeeds
                val pending = CompletableDeferred<RecommendedNovelsByUserTasteResponseDto>()
                tasteRequest = { pending.await() }

                vm.updateNovel(preferencesChanged = true)
                val invalidated = vm.uiState.value!!
                assertEquals(HomeTasteStatus.LOADING, invalidated.tasteStatus)
                assertTrue(invalidated.recommendedNovelsByUserTaste.isEmpty())
                runCurrent()
                assertEquals(HomeTasteStatus.LOADING, vm.uiState.value!!.tasteStatus)
                assertSame(upper, vm.uiState.value!!.popularFeeds)
                assertFalse(vm.uiState.value!!.loading)
                val newCount = if (oldCount == 0) 2 else 0
                pending.complete(taste(newCount))
                runCurrent()
                val updated = vm.uiState.value!!
                assertEquals(if (newCount == 0) HomeTasteStatus.EMPTY else HomeTasteStatus.CONTENT, updated.tasteStatus)
                assertEquals(newCount, updated.recommendedNovelsByUserTaste.size)
            }
        }

    @Test
    fun `cleared owner never receives late data and reentry creates fresh state`() =
        runTest(dispatcher) {
            val pending = CompletableDeferred<RecommendedNovelsByUserTasteResponseDto>()
            tasteRequest = { withContext(NonCancellable) { pending.await() } }
            val old = createViewModel()
            runCurrent()
            val left = old.uiState.value
            owners.last().clear()
            tasteRequest = { taste(0) }
            val next = createViewModel()
            runCurrent()
            pending.complete(taste(10))
            runCurrent()
            assertSame(left, old.uiState.value)
            assertEquals(HomeTasteStatus.EMPTY, next.uiState.value!!.tasteStatus)
        }

    @Test
    fun `terms and permission state remain available after grouped publication`() =
        runTest(dispatcher) {
            val vm = createViewModel(termsChecked = false)
            runCurrent()
            assertTrue(vm.showTermsAgreementDialog.value)
            assertEquals(1, calls["getTermsAgreement"])
            vm.updateTermsAgreementDialogState()
            assertFalse(vm.showTermsAgreementDialog.value)
            vm.updateIsNotificationPermissionFirstLaunched(true)
            runCurrent()
            assertEquals(true, vm.isNotificationPermissionFirstLaunched.value)
            assertEquals(HomeTasteStatus.CONTENT, vm.uiState.value!!.tasteStatus)
            // The existing preferences flow rechecks outstanding terms after a setting changes.
            assertTrue(vm.showTermsAgreementDialog.value)
            assertEquals(2, calls["getTermsAgreement"])
        }

    private fun createViewModel(termsChecked: Boolean = true): HomeViewModel {
        val storage = object : DataStore<Preferences> {
            override val data = MutableStateFlow(
                preferencesOf(
                    UserRepository.TERMS_AGREEMENT_CHECKED_KEY to termsChecked,
                    PushMessageRepository.NOTIFICATION_PERMISSION_FIRST_LAUNCHED_KEY to false,
                ),
            )

            override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
                transform(data.value).also { data.value = it }
        }
        val user = UserRepository(api<UserApi>(), storage)
        return HomeViewModel(
            NovelRepository(api<NovelApi>()),
            FeedRepository(api<FeedApi>()),
            PushMessageRepository(user, AuthRepository(api<AuthApi>()), storage, api<PushMessageApi>()),
            NotificationRepository(api<NotificationApi>()),
            user,
            SavedStateHandle(),
        ).also { vm ->
            owners.add(ViewModelStore().apply { put("home", vm) })
        }
    }

    // Only Retrofit interfaces are doubled; repositories and the ViewModel are production code.
    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> api(): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, args ->
            val call: suspend () -> Any = {
                calls[method.name] = (calls[method.name] ?: 0) + 1
                when (method.name) {
                    "getPopularNovels" -> popularRequest()
                    "getPopularFeeds" -> feedRequest()
                    "getRecommendedNovelsByUserTaste" -> tasteRequest()
                    "getNotificationUnread" -> notificationRequest()
                    "getTermsAgreement" -> TermsAgreementResponseDto(false, false, false)
                    else -> error("Unexpected API call: ${method.name}")
                }
            }
            call.startCoroutineUninterceptedOrReturn(args.last() as Continuation<Any>)
        } as T

    private fun http(code: Int): HttpException = HttpException(Response.error<Unit>(code, "synthetic".toResponseBody()))

    private fun popular(): PopularNovelsResponseDto =
        PopularNovelsResponseDto(listOf(PopularNovelResponseDto(novelId = 1, novelImage = "image", title = "title")))

    private fun feeds(): PopularFeedsResponseDto =
        PopularFeedsResponseDto(
            listOf(PopularFeedResponseDto(1, "content", 0, 0, false, true, "title", "image", "fantasy")),
        )

    private fun taste(count: Int): RecommendedNovelsByUserTasteResponseDto =
        RecommendedNovelsByUserTasteResponseDto(
            (1L..count.toLong()).map { RecommendedNovelByUserTasteResponseDto(it, "title", "author", "image", 0, 0.0, 0) },
        )
}
