package com.into.websoso.ui.notice.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.into.websoso.common.util.clickableWithoutRipple
import com.into.websoso.domain.model.Notice
import com.into.websoso.domain.model.NoticeType

@Composable
fun NoticesContainer(
    notices: List<Notice>,
    isLoadable: Boolean,
    updateNotices: () -> Unit,
    navigateToNoticeDetail: (Long) -> Unit,
    navigateToFeedDetail: (Long) -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, isLoadable) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (index + LOAD_THRESHOLD >= notices.size && isLoadable) {
                    updateNotices()
                }
            }
    }

    LazyColumn(
        state = listState,
    ) {
        notices.forEach { notice ->
            item {
                NoticeCard(
                    notice = notice,
                    modifier = Modifier.clickableWithoutRipple {
                        navigateToDetail(
                            intrinsicId = notice.intrinsicId,
                            noticeType = notice.noticeType,
                            navigateToNoticeDetail = navigateToNoticeDetail,
                            navigateToFeedDetail = navigateToFeedDetail,
                        )
                    },
                )
            }
        }
    }
}

private fun navigateToDetail(
    intrinsicId: Long,
    noticeType: NoticeType,
    navigateToNoticeDetail: (Long) -> Unit,
    navigateToFeedDetail: (Long) -> Unit,
) {
    when (noticeType) {
        NoticeType.NOTICE -> navigateToNoticeDetail(intrinsicId)
        NoticeType.FEED -> navigateToFeedDetail(intrinsicId)
        NoticeType.NONE -> Unit
    }
}

private const val LOAD_THRESHOLD = 5
