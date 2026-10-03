package com.into.websoso.ui.profileEdit

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesOf
import androidx.lifecycle.ViewModelStore
import com.into.websoso.data.remote.api.AvatarApi
import com.into.websoso.data.remote.api.UserApi
import com.into.websoso.data.remote.request.UserProfileEditRequestDto
import com.into.websoso.data.remote.response.AvatarsResponseDto
import com.into.websoso.data.remote.response.MyProfileResponseDto
import com.into.websoso.data.remote.response.UserNicknameValidityResponseDto
import com.into.websoso.data.repository.AvatarRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.domain.usecase.CheckNicknameValidityUseCase
import com.into.websoso.ui.profileEdit.model.AvatarModel
import com.into.websoso.ui.profileEdit.model.Genre
import com.into.websoso.ui.profileEdit.model.ProfileEditResult
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
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
class ProfileEditViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val owners = mutableListOf<ViewModelStore>()
    private val savedRequests = mutableListOf<UserProfileEditRequestDto>()
    private var saveRequest: suspend () -> Unit = {}

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
    fun `nickname introduction and avatar edits do not report a genre change`() =
        runTest(dispatcher) {
            listOf("nickname", "introduction", "avatar").forEach { field ->
                val vm = createViewModel()
                vm.updateUserProfile()
                runCurrent()
                when (field) {
                    "nickname" -> {
                        vm.updateNickname("newname")
                        vm.checkNicknameValidity("newname")
                    }

                    "introduction" -> vm.updateIntroduction("new introduction")
                    "avatar" -> {
                        vm.updateSelectedAvatar(AvatarModel(avatarId = 2))
                        vm.updateRepresentativeAvatar()
                    }
                }
                runCurrent()
                vm.updateProfile()
                runCurrent()

                val result = vm.profileEditUiState.value!!.profileEditResult as ProfileEditResult.Success
                assertFalse(result.genrePreferencesChanged)
                assertEquals(listOf("fantasy", "romance"), savedRequests.last().genrePreferences)
            }
            assertEquals(3, savedRequests.size)
        }

    @Test
    fun `adding removing or clearing genres reports a genre change`() =
        runTest(dispatcher) {
            listOf(listOf(Genre.MYSTERY), listOf(Genre.FANTASY), listOf(Genre.FANTASY, Genre.ROMANCE)).forEach { toggles ->
                val vm = createViewModel()
                vm.updateUserProfile()
                runCurrent()
                toggles.forEach(vm::updateSelectedGenres)
                vm.updateProfile()
                runCurrent()

                val result = vm.profileEditUiState.value!!.profileEditResult as ProfileEditResult.Success
                assertTrue(result.genrePreferencesChanged)
            }
            assertTrue(savedRequests.last().genrePreferences.isEmpty())
        }

    @Test
    fun `restoring the same genres in a different order does not report a change`() =
        runTest(dispatcher) {
            val vm = createViewModel()
            vm.updateUserProfile()
            runCurrent()
            vm.updateSelectedGenres(Genre.FANTASY)
            vm.updateSelectedGenres(Genre.FANTASY)
            vm.updateIntroduction("new introduction")
            vm.updateProfile()
            runCurrent()

            val result = vm.profileEditUiState.value!!.profileEditResult as ProfileEditResult.Success
            assertFalse(result.genrePreferencesChanged)
            assertEquals(listOf("romance", "fantasy"), savedRequests.single().genrePreferences)
        }

    @Test
    fun `success describes the saved request even if selection changes while saving`() =
        runTest(dispatcher) {
            listOf(false, true).forEach { submittedChange ->
                val vm = createViewModel()
                vm.updateUserProfile()
                runCurrent()
                val pending = CompletableDeferred<Unit>()
                saveRequest = { pending.await() }
                if (submittedChange) vm.updateSelectedGenres(Genre.MYSTERY)
                vm.updateIntroduction("new introduction")
                vm.updateProfile()
                runCurrent()
                assertFalse(vm.profileEditUiState.value!!.profileEditResult is ProfileEditResult.Success)

                vm.updateSelectedGenres(Genre.MYSTERY)
                pending.complete(Unit)
                runCurrent()

                val result = vm.profileEditUiState.value!!.profileEditResult as ProfileEditResult.Success
                assertEquals(submittedChange, result.genrePreferencesChanged)
                assertEquals(submittedChange, savedRequests.last().genrePreferences.contains("mystery"))
            }
        }

    @Test
    fun `failed save does not publish success and a successful retry reports the saved change`() =
        runTest(dispatcher) {
            val vm = createViewModel()
            vm.updateUserProfile()
            runCurrent()
            vm.updateSelectedGenres(Genre.MYSTERY)
            saveRequest = { throw IOException("offline") }
            vm.updateProfile()
            runCurrent()
            assertFalse(vm.profileEditUiState.value!!.profileEditResult is ProfileEditResult.Success)

            saveRequest = {}
            vm.updateProfile()
            runCurrent()
            val result = vm.profileEditUiState.value!!.profileEditResult as ProfileEditResult.Success
            assertTrue(result.genrePreferencesChanged)
            assertEquals(2, savedRequests.size)
        }

    private fun createViewModel(): ProfileEditViewModel {
        val storage = object : DataStore<Preferences> {
            override val data = MutableStateFlow(preferencesOf())

            override suspend fun updateData(transform: suspend (Preferences) -> Preferences): Preferences =
                transform(data.value).also { data.value = it }
        }
        return ProfileEditViewModel(
            CheckNicknameValidityUseCase(),
            UserRepository(api<UserApi>(), storage),
            AvatarRepository(api<AvatarApi>()),
        ).also { vm -> owners.add(ViewModelStore().apply { put("profile", vm) }) }
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> api(): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, args ->
            val call: suspend () -> Any = {
                when (method.name) {
                    "getMyProfile" -> MyProfileResponseDto("nickname", "introduction", "", listOf("fantasy", "romance"))
                    "getAvatars" -> AvatarsResponseDto(emptyList())
                    "getNicknameValidity" -> UserNicknameValidityResponseDto(true)
                    "patchProfile" -> {
                        savedRequests.add(args.first() as UserProfileEditRequestDto)
                        saveRequest()
                    }

                    else -> error("Unexpected API call: ${method.name}")
                }
            }
            call.startCoroutineUninterceptedOrReturn(args.last() as Continuation<Any>)
        } as T
}
