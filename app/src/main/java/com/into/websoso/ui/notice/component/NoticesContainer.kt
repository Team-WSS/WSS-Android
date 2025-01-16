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
import com.into.websoso.domain.usecase.GetNoticeListUseCase.Companion.DEFAULT_INTRINSIC_ID

@Composable
fun NoticesContainer(
    notices: List<Notice>,
    isLoadable: Boolean,
    updateNotices: () -> Unit,
    navigateToNoticeDetail: (Notice) -> Unit,
    navigateToFeedDetail: (Notice) -> Unit,
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
                            notice = notice,
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
    notice: Notice,
    navigateToNoticeDetail: (Notice) -> Unit,
    navigateToFeedDetail: (Notice) -> Unit,
) {
    if (notice.intrinsicId == DEFAULT_INTRINSIC_ID) return
    when (notice.noticeType) {
        NoticeType.NOTICE -> navigateToNoticeDetail(notice)
        NoticeType.FEED -> navigateToFeedDetail(notice)
        NoticeType.NONE -> Unit
    }
}

private const val LOAD_THRESHOLD = 5
