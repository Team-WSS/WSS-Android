package com.into.websoso.ui.notification

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.domain.model.Notification
import com.into.websoso.ui.notification.component.NotificationAppBar
import com.into.websoso.ui.notification.component.NotificationsContainer

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    onNotificationDetailClick: (Notification) -> Unit,
    onFeedDetailClick: (Notification) -> Unit,
    onBackButtonClick: () -> Unit,
) {
    val uiState by viewModel.notificationUiState.collectAsStateWithLifecycle()

    BackHandler {
        onBackButtonClick()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        NotificationAppBar(onBackButtonClick)
        NotificationsContainer(
            notifications = uiState.notifications,
            isLoadable = uiState.isLoadable,
            updateNotifications = viewModel::updateNotifications,
            onNotificationDetailClick = onNotificationDetailClick,
            onFeedDetailClick = onFeedDetailClick,
        )
    }
}
