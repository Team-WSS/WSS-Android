package com.into.websoso.ui.notification

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.ui.notification.component.NotificationAppBar
import com.into.websoso.ui.notification.component.NotificationsContainer
import com.into.websoso.ui.notification.model.NotificationModel

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel,
    onNotificationDetailClick: (NotificationModel) -> Unit,
    onFeedDetailClick: (NotificationModel) -> Unit,
    onBackButtonClick: () -> Unit,
) {
    val uiState by viewModel.notificationUIState.collectAsStateWithLifecycle()

    BackHandler {
        onBackButtonClick()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
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
