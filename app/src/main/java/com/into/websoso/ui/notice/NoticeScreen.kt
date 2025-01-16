package com.into.websoso.ui.notice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.domain.model.Notice
import com.into.websoso.ui.notice.component.NoticeAppBar
import com.into.websoso.ui.notice.component.NoticesContainer

@Composable
fun NoticeScreen(
    viewModel: NoticeViewModel,
    onNoticeDetailClick: (Notice) -> Unit,
    onFeedDetailClick: (Notice) -> Unit,
    onBackButtonClick: () -> Unit,
) {
    val uiState by viewModel.noticeUiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        NoticeAppBar(onBackButtonClick)
        NoticesContainer(
            notices = uiState.notices,
            isLoadable = uiState.isLoadable,
            updateNotices = viewModel::updateNotices,
            onNoticeDetailClick = onNoticeDetailClick,
            onFeedDetailClick = onFeedDetailClick,
        )
    }
}
