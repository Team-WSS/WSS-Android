package com.into.websoso.domain.usecase

import com.into.websoso.data.remote.api.NovelNotificationApi
import com.into.websoso.data.remote.request.NovelNotificationSettingRequestDto
import com.into.websoso.data.remote.request.NovelNotificationSubscriptionsDeleteRequestDto
import com.into.websoso.data.remote.response.NovelNotificationSettingResponseDto
import com.into.websoso.data.remote.response.NovelNotificationSubscriptionsResponseDto
import com.into.websoso.data.repository.NovelNotificationRepository
import com.into.websoso.domain.model.NovelNotificationSetting
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class UpdateNovelNotificationSettingUseCaseTest {
    @Test
    fun `저장이 취소되면 CancellationException을 그대로 전파한다`() {
        val useCase = UpdateNovelNotificationSettingUseCase(
            NovelNotificationRepository(FakeNovelNotificationApi(CancellationException())),
        )

        assertThrows(CancellationException::class.java) {
            runBlocking { useCase(NOVEL_ID, NovelNotificationSetting()) }
        }
    }

    @Test
    fun `저장이 실패하면 Result failure를 반환한다`() {
        val useCase = UpdateNovelNotificationSettingUseCase(
            NovelNotificationRepository(FakeNovelNotificationApi(IOException())),
        )

        val result = runBlocking { useCase(NOVEL_ID, NovelNotificationSetting()) }

        assertTrue(result.isFailure)
    }

    @Test
    fun `저장에 성공하면 Result success를 반환한다`() {
        val useCase = UpdateNovelNotificationSettingUseCase(
            NovelNotificationRepository(FakeNovelNotificationApi(null)),
        )

        val result = runBlocking { useCase(NOVEL_ID, NovelNotificationSetting()) }

        assertTrue(result.isSuccess)
    }

    private class FakeNovelNotificationApi(
        private val throwable: Throwable?,
    ) : NovelNotificationApi {
        override suspend fun getNovelNotificationSetting(novelId: Long): NovelNotificationSettingResponseDto = throw NotImplementedError()

        override suspend fun putNovelNotificationSetting(
            novelId: Long,
            novelNotificationSettingRequestDto: NovelNotificationSettingRequestDto,
        ) {
            throwable?.let { throw it }
        }

        override suspend fun getNovelNotificationSubscriptions(
            notificationType: String,
            lastSubscriptionId: Long,
            size: Int,
        ): NovelNotificationSubscriptionsResponseDto = throw NotImplementedError()

        override suspend fun deleteNovelNotificationSubscriptions(
            novelNotificationSubscriptionsDeleteRequestDto: NovelNotificationSubscriptionsDeleteRequestDto,
        ) = throw NotImplementedError()
    }

    companion object {
        private const val NOVEL_ID = 1L
    }
}
