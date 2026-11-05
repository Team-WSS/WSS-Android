package com.into.websoso.core.datastore.datasource.feed

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultPendingFeedLikeStoreTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `손상된 파일은 빈 기록으로 초기화하고 지울 때까지 초기화 표시를 남긴다`() =
        runTest {
            val file = temporaryFolder.newFile(FILE_NAME)
            // 길이 127짜리 값이 온다고 적혀 있지만 내용이 없어 해석할 수 없는 파일
            file.writeBytes(byteArrayOf(0x0A, 0x7F))
            val store = createStore(createDataStore(file))

            assertTrue(store.getPendingLikes().isEmpty())
            assertTrue(store.readResetNotice())
            assertTrue(store.readResetNotice())
            store.clearResetNotice()
            assertFalse(store.readResetNotice())

            store.updatePendingLike(feedId = 1L, isLiked = true)
            assertEquals(mapOf(1L to true), store.getPendingLikes())
        }

    @Test
    fun `해석할 수 없는 기록은 덮어쓰지 않고 보관한 뒤 새 기록을 저장한다`() =
        runTest {
            val dataStore = createDataStore(temporaryFolder.newFile(FILE_NAME))
            dataStore.edit { it[PENDING_FEED_LIKES_KEY] = UNREADABLE_JSON }
            val store = createStore(dataStore)

            store.updatePendingLike(feedId = 1L, isLiked = true)

            assertEquals(mapOf(1L to true), store.getPendingLikes())
            assertEquals(setOf(UNREADABLE_JSON), dataStore.data.first()[PENDING_FEED_LIKES_UNREADABLE_KEY])
            assertTrue(store.readResetNotice())
        }

    @Test
    fun `해석할 수 없는 기록은 복원 시 빈 기록으로 보고 보관한 뒤 지울 때까지 초기화 표시를 남긴다`() =
        runTest {
            val dataStore = createDataStore(temporaryFolder.newFile(FILE_NAME))
            dataStore.edit { it[PENDING_FEED_LIKES_KEY] = UNREADABLE_JSON }
            val store = createStore(dataStore)

            assertTrue(store.getPendingLikes().isEmpty())
            assertTrue(store.readResetNotice())
            store.clearResetNotice()
            assertFalse(store.readResetNotice())
            assertEquals(setOf(UNREADABLE_JSON), dataStore.data.first()[PENDING_FEED_LIKES_UNREADABLE_KEY])
        }

    @Test
    fun `같은 계정이 기록을 다시 가져오면 유지하고 다른 계정이 가져오면 지운다`() =
        runTest {
            val store = createStore(createDataStore(temporaryFolder.newFile(FILE_NAME)))
            store.claimPendingLikes(accountId = 1L)
            store.updatePendingLike(feedId = 1L, isLiked = true)

            store.claimPendingLikes(accountId = 1L)
            assertEquals(mapOf(1L to true), store.getPendingLikes())

            store.claimPendingLikes(accountId = 2L)
            assertTrue(store.getPendingLikes().isEmpty())
        }

    @Test
    fun `계정 정보가 없는 이전 형식의 기록은 어느 계정이 가져와도 지운다`() =
        runTest {
            val dataStore = createDataStore(temporaryFolder.newFile(FILE_NAME))
            dataStore.edit { it[PENDING_FEED_LIKES_KEY] = """{"likes":[{"feedId":1,"isLiked":true}]}""" }
            val store = createStore(dataStore)
            assertEquals(mapOf(1L to true), store.getPendingLikes())

            store.claimPendingLikes(accountId = 1L)

            assertTrue(store.getPendingLikes().isEmpty())
        }

    @Test
    fun `기록을 모두 지우면 같은 계정이 다시 가져와도 남은 기록이 없다`() =
        runTest {
            val store = createStore(createDataStore(temporaryFolder.newFile(FILE_NAME)))
            store.claimPendingLikes(accountId = 1L)
            store.updatePendingLike(feedId = 1L, isLiked = true)

            store.clearPendingLikes()
            store.claimPendingLikes(accountId = 1L)

            assertTrue(store.getPendingLikes().isEmpty())
        }

    private fun TestScope.createDataStore(file: File): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            corruptionHandler = DefaultPendingFeedLikeStore.corruptionHandler,
            scope = backgroundScope,
            produceFile = { file },
        )

    private fun TestScope.createStore(dataStore: DataStore<Preferences>): DefaultPendingFeedLikeStore =
        DefaultPendingFeedLikeStore(dataStore, StandardTestDispatcher(testScheduler))

    companion object {
        private const val FILE_NAME = "pending_feed_like.preferences_pb"

        // 저장 형식이 바뀐 경우처럼, JSON이지만 지금 코드가 해석할 수 없는 값
        private const val UNREADABLE_JSON = """{"likes":"not a list"}"""

        // 저장소 내부 키와 같은 이름으로, 파일에 실제로 무엇이 남는지 확인한다.
        private val PENDING_FEED_LIKES_KEY = stringPreferencesKey("PENDING_FEED_LIKES_KEY")
        private val PENDING_FEED_LIKES_UNREADABLE_KEY = stringSetPreferencesKey("PENDING_FEED_LIKES_UNREADABLE_KEY")
    }
}
