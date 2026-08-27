package com.into.websoso.ui.novelNotification.model

import com.into.websoso.domain.model.NovelNotificationSubscriptions.Companion.DEFAULT_SUBSCRIPTION_ID
import com.into.websoso.domain.model.NovelNotificationType
import com.into.websoso.domain.model.NovelNotificationType.COMPLETION

data class NovelNotificationListUiState(
    val notificationType: NovelNotificationType = COMPLETION,
    val isLoadable: Boolean = true,
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isInitialLoaded: Boolean = false,
    val isEditing: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,
    val lastSubscriptionId: Long = DEFAULT_SUBSCRIPTION_ID,
    val selectedNovelIds: Set<Long> = emptySet(),
    val subscriptions: List<NovelNotificationSubscriptionModel> = emptyList(),
) {
    val isEmpty: Boolean get() = isInitialLoaded && subscriptions.isEmpty()

    val isDeletable: Boolean get() = selectedNovelIds.isNotEmpty()

    val selectedSubscriptions: List<NovelNotificationSubscriptionModel>
        get() = subscriptions.filter { it.novelId in selectedNovelIds }
}
