package com.into.websoso.ui.splash

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.lifecycle.ViewModelStore
import com.into.websoso.data.account.AccountRepository
import com.into.websoso.data.account.datasource.AccountLocalDataSource
import com.into.websoso.data.account.datasource.AccountRemoteDataSource
import com.into.websoso.data.account.model.TokenEntity
import com.into.websoso.data.remote.api.UserApi
import com.into.websoso.data.remote.api.VersionApi
import com.into.websoso.data.remote.response.MinimumVersionResponseDto
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.data.repository.VersionRepository
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

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        owners.forEach(ViewModelStore::clear)
        Dispatchers.resetMain()
    }

    @Test
    fun `fast authentication overlaps the minimum display after version approval`() =
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
            advanceTimeBy(999)
            runCurrent()
            assertEquals("synthetic-new-access", accessToken)
            assertFalse(destination.isCompleted)

            advanceTimeBy(1)
            runCurrent()
            assertTrue(destination.isCompleted)
            assertEquals(UiEffect.NavigateToMain, destination.await())
            assertEquals(1, calls.count { it == "postReissue" })
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
            assertEquals(UiEffect.NavigateToMain, destination.await())
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

    private fun createViewModel(): SplashViewModel {
        val storage = object : DataStore<Preferences> {
            override val data = MutableStateFlow(preferencesOf())

            override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
                transform(data.value).also { data.value = it }
        }
        return SplashViewModel(
            VersionRepository(api<VersionApi>()),
            UserRepository(api<UserApi>(), storage),
            AccountRepository(api<AccountRemoteDataSource>(), api<AccountLocalDataSource>()),
        ).also { vm -> owners.add(ViewModelStore().apply { put("splash", vm) }) }
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
