package com.into.websoso.ui.notificationDetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.data.model.NotificationDetailEntity
import com.into.websoso.ui.notice.component.NoticeAppBar
import com.into.websoso.ui.notificationDetail.component.NotificationDetailBody
import com.into.websoso.ui.notificationDetail.model.NotificationDetailUiState

@Composable
fun NotificationDetailScreen(
    viewModel: NotificationDetailViewModel,
    onBackButtonClick: () -> Unit,
) {
    val uiState by viewModel.notificationDetailUiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        NoticeAppBar(onBackButtonClick)
        NotificationDetailBody(
            uiState = uiState,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationDetailScreenPreview() {
    val uiState = NotificationDetailUiState(
        noticeDetail = NotificationDetailEntity.DEFAULT,
    )

    WebsosoTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            NoticeAppBar(onBackButtonClick = {})
            NotificationDetailBody(
                uiState,
            )
        }
    }
}
