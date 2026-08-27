package com.into.websoso.domain.usecase

import com.into.websoso.data.repository.NovelNotificationRepository
import com.into.websoso.domain.model.NovelNotificationSetting
import javax.inject.Inject

class UpdateNovelNotificationSettingUseCase
    @Inject
    constructor(
        private val novelNotificationRepository: NovelNotificationRepository,
    ) {
        suspend operator fun invoke(
            novelId: Long,
            novelNotificationSetting: NovelNotificationSetting,
        ): Result<Unit> =
            try {
                novelNotificationRepository.saveNovelNotificationSetting(
                    novelId = novelId,
                    isCompletionNotificationEnabled = novelNotificationSetting.isCompletionNotificationEnabled,
                    isHiatusReturnNotificationEnabled = novelNotificationSetting.isHiatusReturnNotificationEnabled,
                )
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
