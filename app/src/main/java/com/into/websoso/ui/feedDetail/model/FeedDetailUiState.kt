package com.into.websoso.ui.feedDetail.model

import com.into.websoso.core.common.ui.model.ResultFrom
import com.into.websoso.core.common.ui.model.ResultFrom.Feed

data class FeedDetailUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val isReported: Boolean = false,
    val isServerError: Boolean = false,
    val isRefreshed: Boolean = false,
    val previousStack: PreviousStack = PreviousStack(Feed),
    val feedDetail: FeedDetailModel = FeedDetailModel(),
) {
    data class PreviousStack(
        val from: ResultFrom,
    )
}
