package com.into.websoso.domain.usecase

import com.into.websoso.data.remote.api.NovelNotificationApi
import com.into.websoso.data.remote.request.NovelNotificationSettingRequestDto
import com.into.websoso.data.remote.request.NovelNotificationSubscriptionsDeleteRequestDto
import com.into.websoso.data.remote.response.NovelNotificationSettingResponseDto
import com.into.websoso.data.remote.response.NovelNotificationSubscriptionsResponseDto
import com.into.websoso.data.repository.NovelNotificationRepository
import com.into.websoso.domain.model.NovelNotificationType.COMPLETION
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class DeleteNovelNotificationSubscriptionsUseCaseTest {
    @Test
    fun `모두 삭제되면 완료로 표시하고 요청한 목록을 그대로 돌려준다`() {
        val api = FakeNovelNotificationApi(failFromCall = null)
        val useCase = DeleteNovelNotificationSubscriptionsUseCase(NovelNotificationRepository(api))

        val result = runBlocking { useCase(COMPLETION, novelIds(150)) }

        val deleteResult = result.getOrThrow()
        assertTrue(deleteResult.isCompleted)
        assertEquals(150, deleteResult.deletedNovelIds.size)
        assertEquals(2, api.deleteCallCount)
    }

    @Test
    fun `두 번째 요청이 실패하면 먼저 삭제된 항목만 돌려준다`() {
        val api = FakeNovelNotificationApi(failFromCall = 2)
        val useCase = DeleteNovelNotificationSubscriptionsUseCase(NovelNotificationRepository(api))

        val result = runBlocking { useCase(COMPLETION, novelIds(150)) }

        val deleteResult = result.getOrThrow()
        assertFalse(deleteResult.isCompleted)
        assertEquals(100, deleteResult.deletedNovelIds.size)
        assertEquals((1L..100L).toList(), deleteResult.deletedNovelIds)
    }

    @Test
    fun `첫 요청부터 실패하면 실패로 돌려준다`() {
        val api = FakeNovelNotificationApi(failFromCall = 1)
        val useCase = DeleteNovelNotificationSubscriptionsUseCase(NovelNotificationRepository(api))

        val result = runBlocking { useCase(COMPLETION, novelIds(150)) }

        assertTrue(result.isFailure)
    }

    private fun novelIds(size: Int): List<Long> = (1L..size.toLong()).toList()

    private class FakeNovelNotificationApi(
        private val failFromCall: Int?,
    ) : NovelNotificationApi {
        var deleteCallCount: Int = 0
            private set

        override suspend fun deleteNovelNotificationSubscriptions(
            novelNotificationSubscriptionsDeleteRequestDto: NovelNotificationSubscriptionsDeleteRequestDto,
        ) {
            deleteCallCount++
            if (failFromCall != null && deleteCallCount >= failFromCall) throw IOException()
        }

        override suspend fun getNovelNotificationSetting(novelId: Long): NovelNotificationSettingResponseDto = throw NotImplementedError()

        override suspend fun putNovelNotificationSetting(
            novelId: Long,
            novelNotificationSettingRequestDto: NovelNotificationSettingRequestDto,
        ) = throw NotImplementedError()

        override suspend fun getNovelNotificationSubscriptions(
            notificationType: String,
            lastSubscriptionId: Long,
            size: Int,
        ): NovelNotificationSubscriptionsResponseDto = throw NotImplementedError()
    }
}
