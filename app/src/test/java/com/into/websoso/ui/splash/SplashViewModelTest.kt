package com.into.websoso.ui.splash

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import com.into.websoso.core.common.util.collectWithLifecycle
import com.into.websoso.data.account.AccountRepository
import com.into.websoso.data.account.datasource.AccountLocalDataSource
import com.into.websoso.data.account.datasource.AccountRemoteDataSource
import com.into.websoso.data.account.model.TokenEntity
import com.into.websoso.data.model.PopularFeedEntity
import com.into.websoso.data.model.PopularNovelsEntity
import com.into.websoso.data.remote.api.UserApi
import com.into.websoso.data.remote.api.VersionApi
import com.into.websoso.data.remote.response.MinimumVersionResponseDto
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.data.repository.VersionRepository
import com.into.websoso.ui.main.home.HomeStartupRequest
import dagger.Lazy
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.lang.reflect.Proxy
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val owners = mutableListOf<ViewModelStore>()
    private val lifecycles = mutableListOf<LifecycleRegistry>()
    private val calls = mutableListOf<String>()
    private var accessToken = "synthetic-access"
    private var refreshToken = "synthetic-refresh"
    private var versionRequest: suspend () -> MinimumVersionResponseDto = {
        MinimumVersionResponseDto("0.0.0", "2026-10-05")
    }
    private var tokenRequest: suspend () -> TokenEntity = {
        delay(300)
        TokenEntity("synthetic-new-access", "synthetic-new-refresh")
    }
    private var startupSession: suspend () -> String = { accessToken }
    private var startedRequests = 0
    private var cancelledRequests = 0
    private val startup by lazy {
        HomeStartupRequest(
            sessionIdentity = {
                calls.add("startupSession")
                startupSession()
            },
            fetchPopular = { pendingResponse<PopularNovelsEntity>() },
            fetchFeeds = { pendingResponse<List<PopularFeedEntity>>() },
            dispatcher = dispatcher,
        )
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        lifecycles.forEach { it.currentState = Lifecycle.State.DESTROYED }
        owners.forEach(ViewModelStore::clear)
        dispatcher.scheduler.runCurrent()
        Dispatchers.resetMain()
    }

    @Test
    fun `successful authentication starts Home requests during the minimum display after version approval`() =
        runTest(dispatcher) {
            versionRequest = {
                delay(200)
                MinimumVersionResponseDto("0.0.0", "2026-10-05")
            }
            val vm = createViewModel()
            val destination = async { vm.uiEffect.first() }
            runCurrent()
            assertFalse(calls.contains("postReissue"))

            advanceTimeBy(200)
            runCurrent()
            assertTrue(calls.contains("postReissue"))
            advanceTimeBy(299)
            runCurrent()
            assertFalse(calls.contains("startupSession"))
            assertEquals(0, startedRequests)

            advanceTimeBy(1)
            runCurrent()
            assertEquals("synthetic-new-access", accessToken)
            assertEquals(2, startedRequests)
            assertFalse(destination.isCompleted)

            advanceTimeBy(699)
            runCurrent()
            assertFalse(destination.isCompleted)

            advanceTimeBy(1)
            runCurrent()
            assertTrue(destination.isCompleted)
            assertNotNull((destination.await() as UiEffect.NavigateToMain).startupRequestId)
            assertEquals(1, calls.count { it == "postReissue" })
            assertEquals(1, calls.count { it == "startupSession" })
        }

    @Test
    fun `slow authentication navigates when ready without another display delay`() =
        runTest(dispatcher) {
            tokenRequest = {
                delay(1500)
                TokenEntity("synthetic-new-access", "synthetic-new-refresh")
            }
            val vm = createViewModel()
            val destination = async { vm.uiEffect.first() }
            runCurrent()
            assertTrue(calls.contains("postReissue"))
            advanceTimeBy(1499)
            runCurrent()
            assertFalse(destination.isCompleted)

            advanceTimeBy(1)
            runCurrent()
            assertTrue(destination.isCompleted)
            assertNotNull((destination.await() as UiEffect.NavigateToMain).startupRequestId)
        }

    @Test
    fun `missing session waits for minimum display and navigates to login without refreshing`() =
        runTest(dispatcher) {
            accessToken = ""
            val vm = createViewModel()
            val destination = async { vm.uiEffect.first() }
            advanceTimeBy(999)
            runCurrent()
            assertFalse(destination.isCompleted)
            assertFalse(calls.contains("postReissue"))

            advanceTimeBy(1)
            runCurrent()
            assertTrue(destination.isCompleted)
            assertEquals(UiEffect.NavigateToLogin, destination.await())
            assertFalse(calls.contains("startupSession"))
        }

    @Test
    fun `failed authentication preserves the minimum display and login destination`() =
        runTest(dispatcher) {
            tokenRequest = {
                delay(300)
                throw IOException("synthetic failure")
            }
            val vm = createViewModel()
            val destination = async { vm.uiEffect.first() }
            advanceTimeBy(999)
            runCurrent()
            assertFalse(destination.isCompleted)

            advanceTimeBy(1)
            runCurrent()
            assertTrue(destination.isCompleted)
            assertEquals(UiEffect.NavigateToLogin, destination.await())
            assertEquals(1, calls.count { it == "postReissue" })
            assertFalse(calls.contains("startupSession"))
        }

    @Test
    fun `required update prevents authentication and navigation`() =
        runTest(dispatcher) {
            versionRequest = { MinimumVersionResponseDto("999999.0.0", "2026-10-05") }
            val vm = createViewModel()
            val destination = async { vm.uiEffect.first() }
            runCurrent()
            assertTrue(destination.isCompleted)
            assertEquals(UiEffect.ShowDialog, destination.await())
            advanceTimeBy(2000)
            runCurrent()
            assertEquals(listOf("getMinimumVersion"), calls)
        }

    @Test
    fun `clearing Splash cancels authentication and prevents late navigation`() =
        runTest(dispatcher) {
            var cancelled = false
            tokenRequest = {
                try {
                    CompletableDeferred<TokenEntity>().await()
                } finally {
                    cancelled = true
                }
            }
            val vm = createViewModel()
            val destination = async { vm.uiEffect.first() }
            runCurrent()
            assertTrue(calls.contains("postReissue"))

            owners.single().clear()
            advanceTimeBy(2000)
            runCurrent()
            assertTrue(cancelled)
            assertFalse(destination.isCompleted)
            destination.cancel()
        }

    @Test
    fun `clearing Splash during the remaining minimum display cancels Home requests and navigation`() =
        runTest(dispatcher) {
            val vm = createViewModel()
            val destination = async { vm.uiEffect.first() }
            advanceTimeBy(300)
            runCurrent()
            assertEquals(2, startedRequests)
            assertFalse(destination.isCompleted)

            owners.single().clear()
            runCurrent()
            assertEquals(2, cancelledRequests)

            advanceTimeBy(1000)
            runCurrent()
            assertFalse(destination.isCompleted)
            destination.cancel()
        }

    @Test
    fun `a deep link during the remaining minimum display cancels Home requests without early navigation`() =
        runTest(dispatcher) {
            val vm = createViewModel()
            val destination = async { vm.uiEffect.first() }
            advanceTimeBy(300)
            runCurrent()
            assertEquals(2, startedRequests)
            assertFalse(destination.isCompleted)

            vm.start(isHomeDestination = false)
            runCurrent()
            assertEquals(2, cancelledRequests)
            assertFalse(destination.isCompleted)

            advanceTimeBy(699)
            runCurrent()
            assertFalse(destination.isCompleted)

            advanceTimeBy(1)
            runCurrent()
            assertTrue(destination.isCompleted)
            assertEquals(UiEffect.NavigateToMain(null), destination.await())
            assertEquals(1, calls.count { it == "postReissue" })
            assertEquals(1, calls.count { it == "startupSession" })
        }

    @Test
    fun `session lookup finishing in background waits for a recreated started collector and navigates once`() =
        runTest(dispatcher) {
            val session = CompletableDeferred<String>()
            startupSession = { session.await() }
            val vm = createViewModel()
            val effects = mutableListOf<UiEffect>()
            val firstActivity = activityLifecycle()
            vm.uiEffect.collectWithLifecycle(firstActivity) { effects.add(it) }
            firstActivity.lifecycle.currentState = Lifecycle.State.STARTED
            advanceTimeBy(300)
            runCurrent()
            assertTrue(calls.contains("startupSession"))
            assertTrue(effects.isEmpty())

            firstActivity.lifecycle.currentState = Lifecycle.State.CREATED
            runCurrent()
            session.complete(accessToken)
            runCurrent()
            assertEquals(2, startedRequests)
            assertTrue(effects.isEmpty())

            advanceTimeBy(700)
            runCurrent()
            assertTrue(effects.isEmpty())

            firstActivity.lifecycle.currentState = Lifecycle.State.DESTROYED
            runCurrent()
            val recreatedActivity = activityLifecycle()
            vm.start(isHomeDestination = true)
            vm.uiEffect.collectWithLifecycle(recreatedActivity) { effect ->
                effects.add(effect)
                vm.onUiEffectHandled(effect)
            }
            runCurrent()
            assertTrue(effects.isEmpty())
            recreatedActivity.lifecycle.currentState = Lifecycle.State.STARTED
            runCurrent()
            val effect = effects.single() as UiEffect.NavigateToMain
            assertNotNull(effect.startupRequestId)
            assertEquals(1, calls.count { it == "postReissue" })
            assertEquals(1, calls.count { it == "startupSession" })

            recreatedActivity.lifecycle.currentState = Lifecycle.State.CREATED
            runCurrent()
            recreatedActivity.lifecycle.currentState = Lifecycle.State.STARTED
            runCurrent()
            assertEquals(1, effects.size)

            // Main may receive the ID before Home is ready to take its requests.
            owners.single().clear()
            runCurrent()
            assertEquals(0, cancelledRequests)
            val transferred = startup.take(effect.startupRequestId)
            assertNotNull(transferred)
            transferred!!.cancel()
            runCurrent()
            assertEquals(2, cancelledRequests)
        }

    @Test
    fun `an unhandled destination is retained and clearing Splash discards its requests`() =
        runTest(dispatcher) {
            val vm = createViewModel()
            advanceTimeBy(1000)
            runCurrent()
            val effect = vm.uiEffect.first() as UiEffect.NavigateToMain
            assertEquals(effect, vm.uiEffect.first())
            assertEquals(2, startedRequests)

            owners.single().clear()
            runCurrent()
            assertEquals(2, cancelledRequests)
            assertNull(startup.take(effect.startupRequestId))
        }

    @Test
    fun `failed navigation discards the untransferred request and destination`() =
        runTest(dispatcher) {
            val vm = createViewModel()
            advanceTimeBy(1000)
            runCurrent()
            val effect = vm.uiEffect.first() as UiEffect.NavigateToMain

            vm.onNavigationFailed()
            runCurrent()
            assertEquals(2, cancelledRequests)
            assertNull(startup.take(effect.startupRequestId))
            val next = async { vm.uiEffect.first() }
            runCurrent()
            assertFalse(next.isCompleted)
            next.cancel()
        }

    @Test
    fun `collection destination skips startup requests`() =
        runTest(dispatcher) {
            val vm = createViewModel(isHomeDestination = false)
            advanceTimeBy(1000)
            runCurrent()
            assertEquals(UiEffect.NavigateToMain(null), vm.uiEffect.first())
            assertFalse(calls.contains("startupSession"))
            assertEquals(0, startedRequests)
        }

    @Test
    fun `a deep link arriving during session lookup prevents transfer`() =
        runTest(dispatcher) {
            val session = CompletableDeferred<String>()
            startupSession = { session.await() }
            val vm = createViewModel()
            advanceTimeBy(1000)
            runCurrent()
            assertTrue(calls.contains("startupSession"))

            vm.start(isHomeDestination = false)
            session.complete(accessToken)
            runCurrent()
            assertEquals(UiEffect.NavigateToMain(null), vm.uiEffect.first())
            assertEquals(0, startedRequests)
        }

    @Test
    fun `a deep link arriving after preparation discards requests and replaces the pending ID`() =
        runTest(dispatcher) {
            val vm = createViewModel()
            advanceTimeBy(1000)
            runCurrent()
            val effect = vm.uiEffect.first() as UiEffect.NavigateToMain

            vm.start(isHomeDestination = false)
            runCurrent()
            assertEquals(UiEffect.NavigateToMain(null), vm.uiEffect.first())
            assertNull(startup.take(effect.startupRequestId))
            assertEquals(2, cancelledRequests)
            assertEquals(1, calls.count { it == "postReissue" })
        }

    @Test
    fun `clearing Splash during session lookup cancels preparation without a destination`() =
        runTest(dispatcher) {
            var cancelled = false
            startupSession = {
                try {
                    CompletableDeferred<String>().await()
                } finally {
                    cancelled = true
                }
            }
            val vm = createViewModel()
            val destination = async { vm.uiEffect.first() }
            advanceTimeBy(1000)
            runCurrent()
            assertTrue(calls.contains("startupSession"))

            owners.single().clear()
            runCurrent()
            assertTrue(cancelled)
            assertEquals(0, startedRequests)
            assertFalse(destination.isCompleted)
            destination.cancel()
        }

    @Test
    fun `failed startup session lookup still navigates without a request ID`() =
        runTest(dispatcher) {
            startupSession = { throw IOException("synthetic setup failure") }
            val vm = createViewModel()
            advanceTimeBy(1000)
            runCurrent()
            assertEquals(UiEffect.NavigateToMain(null), vm.uiEffect.first())
            assertEquals(0, startedRequests)
        }

    private fun activityLifecycle() =
        object : LifecycleOwner {
            override val lifecycle = LifecycleRegistry.createUnsafe(this).also {
                it.currentState = Lifecycle.State.CREATED
                lifecycles.add(it)
            }
        }

    private suspend fun <T> pendingResponse(): T {
        startedRequests++
        try {
            return CompletableDeferred<T>().await()
        } finally {
            cancelledRequests++
        }
    }

    private fun createViewModel(isHomeDestination: Boolean = true): SplashViewModel {
        val storage = object : DataStore<Preferences> {
            override val data = MutableStateFlow(preferencesOf())

            override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
                transform(data.value).also { data.value = it }
        }
        return SplashViewModel(
            VersionRepository(api<VersionApi>()),
            UserRepository(api<UserApi>(), storage),
            AccountRepository(api<AccountRemoteDataSource>(), api<AccountLocalDataSource>()),
            Lazy { startup },
        ).also { vm ->
            owners.add(ViewModelStore().apply { put("splash", vm) })
            vm.start(isHomeDestination)
        }
    }

    // Keep repositories real; replace only their external data sources with synthetic inputs.
    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> api(): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, args ->
            val call: suspend () -> Any = {
                calls.add(method.name)
                when (method.name) {
                    "getMinimumVersion" -> versionRequest()
                    "postReissue" -> tokenRequest()
                    "selectAccessToken" -> accessToken
                    "selectRefreshToken" -> refreshToken
                    "updateAccessToken" -> accessToken = args.first() as String
                    "updateRefreshToken" -> refreshToken = args.first() as String
                    else -> error("Unexpected data source call: ${method.name}")
                }
            }
            call.startCoroutineUninterceptedOrReturn(args.last() as Continuation<Any>)
        } as T
}
