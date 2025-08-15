package com.into.websoso.ui.notificationDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.data.model.NotificationDetailEntity
import com.into.websoso.ui.notification.component.NotificationAppBar
import com.into.websoso.ui.notificationDetail.component.NotificationDetailContent
import com.into.websoso.ui.notificationDetail.model.NotificationDetailUiState

@Composable
fun NotificationDetailScreen(
    viewModel: NotificationDetailViewModel,
    onBackButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.notificationDetailUiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        NotificationAppBar(onBackButtonClick)
        NotificationDetailContent(
            uiState = uiState,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationDetailScreenPreview() {
    val uiState = NotificationDetailUiState(
        notificationDetail = NotificationDetailEntity.DEFAULT,
    )

    WebsosoTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            NotificationAppBar(onBackButtonClick = {})
            NotificationDetailContent(
                uiState,
            )
        }
    }
}
