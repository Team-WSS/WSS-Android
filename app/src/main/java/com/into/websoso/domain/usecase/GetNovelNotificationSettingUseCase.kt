package com.into.websoso.domain.usecase

import com.into.websoso.data.repository.NovelNotificationRepository
import com.into.websoso.domain.mapper.toDomain
import com.into.websoso.domain.model.NovelNotificationSetting
import javax.inject.Inject

class GetNovelNotificationSettingUseCase
    @Inject
    constructor(
        private val novelNotificationRepository: NovelNotificationRepository,
    ) {
        suspend operator fun invoke(novelId: Long): Result<NovelNotificationSetting> =
            try {
                Result.success(
                    novelNotificationRepository.fetchNovelNotificationSetting(novelId).toDomain(),
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
    }
