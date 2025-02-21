package com.into.websoso.ui.notification.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.domain.model.NotificationType
import com.into.websoso.domain.usecase.GetNotificationListUseCase.Companion.DEFAULT_INTRINSIC_ID
import com.into.websoso.ui.notification.model.NotificationUiModel

private const val LOAD_THRESHOLD = 5

@Composable
fun NotificationsContainer(
    notifications: List<NotificationUiModel>,
    isLoadable: Boolean,
    updateNotifications: () -> Unit,
    onNotificationDetailClick: (NotificationUiModel) -> Unit,
    onFeedDetailClick: (NotificationUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, isLoadable) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (index + LOAD_THRESHOLD >= notifications.size && isLoadable) {
                    updateNotifications()
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = modifier,
    ) {
        notifications.forEach { notification ->
            item {
                NotificationCard(
                    notification = notification,
                    modifier = Modifier.clickableWithoutRipple {
                        navigateToDetail(
                            notification = notification,
                            onNotificationDetailClick = onNotificationDetailClick,
                            onFeedDetailClick = onFeedDetailClick,
                        )
                    },
                )
            }
        }
    }
}

private fun navigateToDetail(
    notification: NotificationUiModel,
    onNotificationDetailClick: (NotificationUiModel) -> Unit,
    onFeedDetailClick: (NotificationUiModel) -> Unit,
) {
    if (notification.intrinsicId == DEFAULT_INTRINSIC_ID) return
    when (notification.notificationType) {
        NotificationType.NOTICE -> onNotificationDetailClick(notification)
        NotificationType.FEED -> onFeedDetailClick(notification)
        NotificationType.NONE -> Unit
    }
}

@Preview
@Composable
private fun NotificationsContainerPreview() {
    WebsosoTheme {
        NotificationsContainer(
            notifications = emptyList(),
            isLoadable = true,
            updateNotifications = {},
            onNotificationDetailClick = {},
            onFeedDetailClick = {},
        )
    }
}
