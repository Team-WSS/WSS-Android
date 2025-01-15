package com.into.websoso.ui.notice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.ui.notice.component.NoticeAppBar
import com.into.websoso.ui.notice.component.NoticesContainer

@Composable
fun NoticeScreen(
    viewModel: NoticeViewModel,
    navigateToNoticeDetail: (Long) -> Unit,
    navigateToFeedDetail: (Long) -> Unit,
) {
    val uiState by viewModel.noticeUiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        NoticeAppBar()
        NoticesContainer(
            notices = uiState.notices,
            isLoadable = uiState.isLoadable,
            updateNotices = viewModel::updateNotices,
            navigateToNoticeDetail = navigateToNoticeDetail,
            navigateToFeedDetail = navigateToFeedDetail,
        )
    }
}
