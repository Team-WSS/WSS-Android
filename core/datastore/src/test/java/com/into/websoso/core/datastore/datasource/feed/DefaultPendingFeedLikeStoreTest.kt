package com.into.websoso.core.datastore.datasource.feed

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultPendingFeedLikeStoreTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `손상된 파일은 빈 기록으로 초기화하고 초기화 표시를 한 번만 알린다`() =
        runTest {
            val file = temporaryFolder.newFile("pending_feed_like.preferences_pb")
            // 길이 127짜리 값이 온다고 적혀 있지만 내용이 없어 해석할 수 없는 파일
            file.writeBytes(byteArrayOf(0x0A, 0x7F))
            val dataStore = PreferenceDataStoreFactory.create(
                corruptionHandler = DefaultPendingFeedLikeStore.corruptionHandler,
                scope = backgroundScope,
                produceFile = { file },
            )
            val store = DefaultPendingFeedLikeStore(dataStore, StandardTestDispatcher(testScheduler))

            assertTrue(store.getPendingLikes().isEmpty())
            assertTrue(store.consumeResetNotice())
            assertFalse(store.consumeResetNotice())

            store.updatePendingLike(feedId = 1L, isLiked = true)
            assertEquals(mapOf(1L to true), store.getPendingLikes())
        }
}
