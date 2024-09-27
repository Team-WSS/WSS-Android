package com.teamwss.websoso.common.ui.model

enum class ResultFrom {
    FeedDetailBack,
    FeedDetailRemoved,
    FeedDetailRefreshed,
    CreateFeed,
    Feed,
    ;

    val RESULT_OK: Int = ordinal
}
