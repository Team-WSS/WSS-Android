package com.into.websoso.ui.notice.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.into.websoso.ui.notice.model.NoticeUiState

@Composable
fun NoticesContainer(uiState: NoticeUiState) {
    LazyColumn {
        uiState.noticeInfo.notices.forEach { notice ->
            item {
                NoticeCard(notice)
            }
        }
    }
}
