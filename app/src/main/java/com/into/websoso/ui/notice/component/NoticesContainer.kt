package com.into.websoso.ui.notice.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.into.websoso.domain.model.Notice

@Composable
fun NoticesContainer(
    notices: List<Notice>,
    isLoadable: Boolean,
    updateNotices: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (index + LOAD_THRESHOLD >= notices.size && isLoadable) {
                    updateNotices()
                }
            }
    }

    LazyColumn {
        notices.forEach { notice ->
            item {
                NoticeCard(notice)
            }
        }
    }
}

private const val LOAD_THRESHOLD = 10
